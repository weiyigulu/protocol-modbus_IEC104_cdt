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
 * AABB 的modbus 4 字节 有符号 整型数据
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class PM_ABCD extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.PM_ABCD;
	}

	public PM_ABCD(BigDecimal value) {
		super(value);
	}

	@Override
	public PM_ABCD decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(new Integer(((bytes[offset * 2] & 0xff) << 24) | ((bytes[offset * 2 + 1] & 0xff) << 16)
				| ((bytes[offset * 2 + 2] & 0xff) << 8) | (bytes[offset * 2 + 3] & 0xff)));
		return this;
	}

	@Override
	public PM_ABCD decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf(new Integer(((byteBuf.get() & 0xff) << 24) | ((byteBuf.get() & 0xff) << 16)
				| ((byteBuf.get() & 0xff) << 8) | (byteBuf.get() & 0xff)));
		return this;
	}

	@Override
	public PM_ABCD encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.intValue() >> 24));
		bytes.add((byte) (this.value.intValue() >> 16));
		bytes.add((byte) (this.value.intValue() >> 8));
		bytes.add((byte) this.value.intValue());
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.intValue() >> 24), (byte) (this.value.intValue() >> 16)));
		registers.add(new Register((byte) (this.value.intValue() >> 8), (byte) this.value.intValue()));
		return registers;
	}
}
