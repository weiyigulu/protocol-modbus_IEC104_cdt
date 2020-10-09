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
 * AABB 的modbus 4 字节 无符号 整型数据
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class P_ABCD extends NumericModbusData {

	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.P_ABCD;
	}

	public P_ABCD(BigDecimal value) {
		super(value);
	}

	@Override
	public P_ABCD decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(new Long(((long) ((bytes[offset * 2] & 0xff)) << 24) | ((long) ((bytes[offset * 2 + 1] & 0xff)) << 16)
				| ((long) ((bytes[offset * 2 + 2] & 0xff)) << 8) | ((bytes[offset * 2 + 3] & 0xff))));
		return this;
	}

	@Override
	public P_ABCD decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf(new Long(((long) ((byteBuf.get() & 0xff)) << 24) | ((long) ((byteBuf.get() & 0xff)) << 16)
				| ((long) ((byteBuf.get() & 0xff)) << 8) | (byteBuf.get() & 0xff)));
		return this;
	}

	@Override
	public P_ABCD encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.longValue() >> 24));
		bytes.add((byte) (this.value.longValue() >> 16));
		bytes.add((byte) (this.value.longValue() >> 8));
		bytes.add((byte) this.value.longValue());
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.longValue() >> 24), (byte) (this.value.longValue() >> 16)));
		registers.add(new Register((byte) (this.value.longValue() >> 8), (byte) this.value.longValue()));
		return registers;
	}
}
