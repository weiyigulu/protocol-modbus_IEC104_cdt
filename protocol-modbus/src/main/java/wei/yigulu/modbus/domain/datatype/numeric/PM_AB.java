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
 * AB  ±AB的数据类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class PM_AB extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.PM_AB;
	}

	public PM_AB(BigDecimal value) {
		super(value);
	}

	@Override
	public PM_AB decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf((short) (((bytes[offset * 2] & 0xff) << 8) | (bytes[offset * 2 + 1] & 0xff)));
		return this;
	}

	@Override
	public PM_AB decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf((short) (((byteBuf.get() & 0xff) << 8) | (byteBuf.get() & 0xff)));
		return this;
	}

	@Override
	public PM_AB encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.shortValue() >> 8));
		bytes.add((byte) this.value.shortValue());
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.shortValue() >> 8), (byte) this.value.shortValue()));
		return registers;
	}
}
