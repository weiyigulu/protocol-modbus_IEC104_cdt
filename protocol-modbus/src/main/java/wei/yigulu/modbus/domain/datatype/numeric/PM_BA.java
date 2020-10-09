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
 * BA ±BA的数据类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class PM_BA extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.PM_BA;
	}

	public PM_BA(BigDecimal value) {
		super(value);
	}

	@Override
	public PM_BA decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf((short) (((bytes[offset * 2 + 1] & 0xff) << 8) | (bytes[offset * 2] & 0xff)));
		return this;
	}

	@Override
	public PM_BA decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf((short) ((byteBuf.get() & 0xff) | ((byteBuf.get() & 0xff) << 8)));
		return this;
	}

	@Override
	public PM_BA encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.shortValue()));
		bytes.add((byte) (this.value.shortValue() >> 8));
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) this.value.shortValue(), (byte) (this.value.shortValue() >> 8)));
		return registers;
	}
}
