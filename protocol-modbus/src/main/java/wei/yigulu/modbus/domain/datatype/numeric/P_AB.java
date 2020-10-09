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
 * AB +AB 数据类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public class P_AB extends NumericModbusData {

	{
		super.modbusDataTypeEnum = ModbusDataTypeEnum.P_AB;
	}

	public P_AB(BigDecimal value) {
		super(value);
	}

	@Override
	public P_AB decode(byte[] bytes, int offset) {
		this.value = BigDecimal.valueOf(((bytes[offset * 2] & 0xff) << 8) | (bytes[offset * 2 + 1] & 0xff));
		return this;
	}


	@Override
	public P_AB decode(ByteBuffer byteBuf) {
		this.value = BigDecimal.valueOf(((byteBuf.get() & 0xff) << 8) | (byteBuf.get() & 0xff));
		return this;
	}

	@Override
	public P_AB encode(List<Byte> bytes) {
		bytes.add((byte) (this.value.intValue() >> 8));
		bytes.add((byte) this.value.intValue());
		return this;
	}


	public static byte[] decode(int i) {
		byte[] bs = new byte[2];
		bs[0] = (byte) (i >> 8);
		bs[1] = (byte) i;
		return bs;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register((byte) (this.value.intValue() >> 8), (byte) this.value.intValue()));
		return registers;
	}
}
