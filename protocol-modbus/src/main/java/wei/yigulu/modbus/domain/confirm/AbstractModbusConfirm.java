package wei.yigulu.modbus.domain.confirm;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusPacketInterface;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @program: protocol
 * @description: modbus的控制命令
 * @author: xiuwei
 * @create: 2021-04-26 16:14
 */
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractModbusConfirm implements ModbusPacketInterface {


	/**
	 * 客户端地址 一字节
	 */
	protected Integer slaveId = 01;

	/**
	 * 功能码  一字节  5，6，15，16
	 */
	protected FunctionCode functionCode;

	/**
	 * 下达数据的起始地址位  两字节
	 */
	protected Integer startAddress;

	/**
	 * 输出数据的数量（15，16）  两字节
	 */
	protected Integer quantity;

	/**
	 * 输出数据 （5，6）  两字节
	 */
	protected byte[] b2= new byte[2];


	public Integer getLength() {
		return 8;
	}


	@Override
	public AbstractModbusConfirm encode(List<Byte> bytes) throws ModbusException {
		bytes.add((byte) (slaveId & 0xff));
		bytes.add((byte) (functionCode.getCode() & 0xff));
		new P_AB(BigDecimal.valueOf(startAddress)).encode(bytes);
		if (functionCode == FunctionCode.WRITE_COIL || functionCode == FunctionCode.WRITE_REGISTER) {
			bytes.add(b2[0]);
			bytes.add(b2[1]);
		}else {
			new P_AB(BigDecimal.valueOf(quantity)).encode(bytes);
		}
		return this;
	}


	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲区
	 * @return
	 */
	@Override
	public AbstractModbusConfirm decode(ByteBuffer byteBuf) throws ModbusException {
		slaveId = byteBuf.get() & 0xff;
		functionCode = FunctionCode.valueOf(byteBuf.get() & 0xff);
		if (functionCode.getCode() > 0x80) {
			int i = byteBuf.get() & 0xff;
			if (functionCode.getCode().equals(0x89) || functionCode.getCode().equals(0x90)) {
				switch (i) {
					case 1:
						throw new ModbusException("功能码异常");
					case 3:
						throw new ModbusException("输出数据数量或字节数异常");
					case 2:
						throw new ModbusException("地址异常或输出数量异常");
					case 4:
						throw new ModbusException("写入过程异常");
				}
			} else {
				switch (i) {
					case 1:
						throw new ModbusException("功能码异常");
					case 3:
						throw new ModbusException("数据值异常");
					case 2:
						throw new ModbusException("地址异常");
					case 4:
						throw new ModbusException("写入过程异常");
				}
			}
		}
		startAddress = new P_AB().decode(byteBuf).getValue().intValue();
		if (functionCode == FunctionCode.WRITE_COIL || functionCode == FunctionCode.WRITE_REGISTER) {
			b2 = new byte[2];
			byteBuf.get(b2);
		} else {
			quantity = new P_AB().decode(byteBuf).getValue().intValue();
		}
		return this;
	}


}
