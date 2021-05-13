package wei.yigulu.modbus.domain.datatype.numeric;

import lombok.NoArgsConstructor;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.datatype.NumericModbusData;
import wei.yigulu.modbus.domain.datatype.Register;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * BADC 的modbus 4 字节 浮点型数据
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class BADC extends NumericModbusData {

	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.BADC;
	}

	public BADC(BigDecimal value) {
		super(value);
	}

	@Override
	public BADC decode(byte[] bytes, int offset) {
		this.value = new BigDecimal(Float.toString(Float.intBitsToFloat(((bytes[offset * 2 + 0] & 0xff) << 16) | ((bytes[offset * 2 + 1] & 0xff) << 24) | (bytes[offset * 2 + 2] & 0xff) | ((bytes[offset * 2 + 3] & 0xff) << 8)
		)));
		return this;
	}

	@Override
	public BADC decode(ByteBuffer byteBuf) {
		this.value = new BigDecimal(Float.toString(Float.intBitsToFloat(
				((byteBuf.get() & 0xff) << 16) | ((byteBuf.get() & 0xff) << 24) | (byteBuf.get() & 0xff) | ((byteBuf.get() & 0xff) << 8))));
		return this;
	}

	@Override
	public BADC encode(List<Byte> bytes) {
		int tempVal = Float.floatToIntBits(this.value.floatValue());
		bytes.add((byte) (tempVal >> 16));
		bytes.add((byte) (tempVal >> 24));
		bytes.add((byte) tempVal);
		bytes.add((byte) (tempVal >> 8));
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		int tempVal = Float.floatToIntBits(this.value.floatValue());
		registers.add(new Register((byte) (tempVal >> 16), (byte) (tempVal >> 24)));
		registers.add(new Register((byte) (tempVal), (byte) (tempVal >> 8)));
		return registers;
	}
}
