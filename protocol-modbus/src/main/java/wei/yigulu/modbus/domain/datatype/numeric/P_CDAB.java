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
 * BBAA 的modbus 4 字节 无符号 整型数据
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class P_CDAB extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.P_CDAB;
	}

	public P_CDAB(BigDecimal value) {
		super(value);
	}

	@Override
	public P_CDAB decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(new Long(((long) ((bytes[offset * 2 + 2] & 0xff)) << 24) | ((long) ((bytes[offset * 2 + 3] & 0xff)) << 16)
				| ((long) ((bytes[offset * 2] & 0xff)) << 8) | (long) ((bytes[offset * 2 + 1] & 0xff))));
		return this;
	}

	@Override
	public P_CDAB decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf(new Long((((long) ((byteBuf.get() & 0xff)) << 8) | (long) ((byteBuf.get() & 0xff))) | (long) ((byteBuf.get() & 0xff)) << 24) | ((long) ((byteBuf.get() & 0xff)) << 16)
		);
		return this;
	}

	@Override
	public P_CDAB encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.longValue() >> 8));
		bytes.add((byte) this.value.longValue());
		bytes.add((byte) (this.value.longValue() >> 24));
		bytes.add((byte) (this.value.longValue() >> 16));
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.longValue() >> 8), (byte) this.value.longValue()));
		registers.add(new Register((byte) (this.value.longValue() >> 24), (byte) (this.value.longValue() >> 16)));
		return registers;
	}
}
