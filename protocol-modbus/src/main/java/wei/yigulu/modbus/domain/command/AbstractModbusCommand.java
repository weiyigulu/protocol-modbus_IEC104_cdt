package wei.yigulu.modbus.domain.command;

import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusPacketInterface;
import wei.yigulu.modbus.domain.datatype.BooleanModbusDataInCoil;
import wei.yigulu.modbus.domain.datatype.Register;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.datatype.numeric.SingleCommandCoilValue;
import wei.yigulu.modbus.exceptiom.ModbusException;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @program: protocol
 * @description: modbus的控制命令
 * @author: xiuwei
 * @create: 2021-04-26 16:14
 */
public abstract class AbstractModbusCommand implements ModbusPacketInterface {


	public static  final HashSet<Byte> FUNCTION_CODES=new HashSet(Arrays.asList(new Byte[]{0x05, (byte) 0x85, 0x06, (byte) 0x86, 0x0F, (byte) 0x8F, 0x10, (byte) 0x90, 0x17, (byte) 0x97}));


	public static final int R_MAX_NUM = 120;

	public static final int C_MAX_NUM = 1968;

	/**
	 * 客户端地址 一字节
	 */
	@Setter
	@Getter
	@Accessors(chain = true)
	protected Integer slaveId = 01;

	/**
	 * 功能码  一字节
	 */
	@Getter
	protected FunctionCode functionCode;

	/**
	 * 下达数据的起始地址位  两字节
	 */
	@Getter
	protected Integer startAddress;

	/**
	 * 输出数据的数量  两字节  （线圈或者寄存器的数量） 两字节  15 16 有    5 6 没有
	 */
	@Getter
	protected Integer quantity;

	/**
	 * 输出数据的字节数  一字节（线圈数量*1或寄存器数量*2） 15 16 有    5 6 没有
	 */
	@Getter
	protected Integer numOfByte;

	/**
	 * 具体输出值的内容
	 */
	@Getter
	protected byte[] dataBytes;


	public Integer getLength() {
		if (functionCode == FunctionCode.WRITE_COIL) {
			return 6;
		} else if (functionCode == FunctionCode.WRITE_REGISTER) {
			return 6;
		} else {
			return 7 + dataBytes.length;
		}
	}


	public AbstractModbusCommand setRegisters(@Nonnull Integer startAddress, @Nonnull List<RegisterValue> values) {
		if (values.size() == 0) {
			throw new RuntimeException("未传入具体的控制值");
		}
		this.startAddress = startAddress;
		List<Register> registers = new ArrayList<>();
		for (RegisterValue rv : values) {
			registers.addAll(rv.getRegisters());
		}
		if (registers.size() > R_MAX_NUM) {
			throw new RuntimeException("传入寄存器的数量超过120个");
		}
		if (registers.size() > 1) {
			functionCode = FunctionCode.WRITE_REGISTERS;
			this.quantity = registers.size();
			this.numOfByte = registers.size() * 2;
			dataBytes = new byte[numOfByte];
			for (int i = 0; i < registers.size(); i++) {
				this.dataBytes[i * 2] = registers.get(i).getB1();
				this.dataBytes[i * 2 + 1] = registers.get(i).getB2();
			}
		} else {
			functionCode = FunctionCode.WRITE_REGISTER;
			this.quantity = null;
			this.numOfByte = null;
			dataBytes = new byte[2];
			this.dataBytes[0] = registers.get(0).getB1();
			this.dataBytes[1] = registers.get(0).getB2();
		}
		return this;
	}


	public AbstractModbusCommand setCoils(@Nonnull Integer startAddress, @Nonnull List<Boolean> values) {
		if (values.size() == 0) {
			throw new RuntimeException("未传入具体的控制值");
		}
		if (values.size() > C_MAX_NUM) {
			throw new RuntimeException("传入线圈的数量超过1968个");
		}
		this.startAddress = startAddress;
		if (values.size() == 1) {
			functionCode = FunctionCode.WRITE_COIL;
			this.quantity = null;
			this.numOfByte = null;
			dataBytes = new SingleCommandCoilValue(values.get(0)).encode();
		} else {
			functionCode = FunctionCode.WRITE_COILS;
			this.quantity = values.size();
			List<BooleanModbusDataInCoil> booleanModbusDataInCoils = BooleanModbusDataInCoil.getFormBooleanList(values);
			this.numOfByte = booleanModbusDataInCoils.size();
			List<Byte> byteList = new ArrayList<>();
			booleanModbusDataInCoils.forEach(o -> o.encode(byteList));
			this.dataBytes = Bytes.toArray(byteList);
		}
		return this;
	}


	@Override
	public AbstractModbusCommand encode(List<Byte> bytes) throws ModbusException {
		bytes.add((byte) (slaveId & 0xff));
		bytes.add((byte) (functionCode.getCode() & 0xff));
		new P_AB(BigDecimal.valueOf(startAddress)).encode(bytes);
		if (quantity != null) {
			new P_AB(BigDecimal.valueOf(quantity)).encode(bytes);
		}
		if (numOfByte != null) {
			bytes.add((byte) (numOfByte & 0xff));
		}
		bytes.addAll(Bytes.asList(dataBytes));
		return this;
	}

	@Override
	public AbstractModbusCommand decode(ByteBuffer byteBuf) throws ModbusException {
		this.slaveId=(int)byteBuf.get();
		this.functionCode=FunctionCode.valueOf(byteBuf.get());
		this.startAddress=(int)byteBuf.getShort();
		if(this.functionCode==FunctionCode.WRITE_COILS ||this.functionCode==FunctionCode.WRITE_REGISTERS){
			this.quantity=(int)byteBuf.getShort();
			this.numOfByte=(int)byteBuf.get();
			this.dataBytes=new byte[this.numOfByte];
			byteBuf.get(dataBytes);
		}else{
			this.dataBytes=new byte[2];
			byteBuf.get(dataBytes);
		}
		return this;
	}



}
