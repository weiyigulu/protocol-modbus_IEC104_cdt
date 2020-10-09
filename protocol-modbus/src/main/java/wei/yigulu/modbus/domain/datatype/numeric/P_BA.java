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
 * BA +BA 的数据类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class P_BA extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.P_BA;
	}

	public P_BA(BigDecimal value) {
		super(value);
	}

	public static byte[] decode(int i) {
		byte[] bs = new byte[2];
		bs[0] = (byte) i;
		bs[1] = (byte) (i >> 8);
		return bs;
	}

	@Override
	public P_BA decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(((bytes[offset * 2 + 1] & 0xff) << 8) | (bytes[offset * 2] & 0xff));
		return this;
	}

	@Override
	public P_BA decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf((byteBuf.get() & 0xff) | ((byteBuf.get() & 0xff) << 8));
		return this;
	}

	@Override
	public P_BA encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.intValue()));
		bytes.add((byte) (this.value.intValue() >> 8));
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) this.value.intValue(), (byte) (this.value.intValue() >> 8)));
		return registers;
	}
}
