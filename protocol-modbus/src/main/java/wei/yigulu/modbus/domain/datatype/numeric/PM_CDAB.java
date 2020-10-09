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
 * BBAA 的modbus 4 字节 有符号 整型数据
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class PM_CDAB extends NumericModbusData {
	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.CDAB;
	}

	public PM_CDAB(BigDecimal value) {
		super(value);
	}

	@Override
	public PM_CDAB decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(new Integer(((bytes[offset * 2 + 2] & 0xff) << 24) | ((bytes[offset * 2 + 3] & 0xff) << 16)
				| ((bytes[offset * 2] & 0xff) << 8) | (bytes[offset * 2 + 1] & 0xff)));
		return this;
	}

	@Override
	public PM_CDAB decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf(new Integer(((byteBuf.get() & 0xff) << 8) | (byteBuf.get() & 0xff)) | ((byteBuf.get() & 0xff) << 24) | ((byteBuf.get() & 0xff) << 16)
		);
		return this;
	}

	@Override
	public PM_CDAB encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.intValue() >> 8));
		bytes.add((byte) this.value.intValue());
		bytes.add((byte) (this.value.intValue() >> 24));
		bytes.add((byte) (this.value.intValue() >> 16));
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.intValue() >> 8), (byte) this.value.intValue()));
		registers.add(new Register((byte) (this.value.intValue() >> 24), (byte) (this.value.intValue() >> 16)));
		return registers;
	}
}
