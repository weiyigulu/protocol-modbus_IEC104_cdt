package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 16个布尔值组成的数据类型 仅对应 01 和 02 功能码
 *
 * @author: xiuwei
 * @version:
 */
public class BooleanModbusDataInRegister extends RegisterValue {

	/**
	 * 一个字节存放布尔值个数
	 */
	private static final int ONEBYTEBOOL = 8;

	/**
	 * 两个字节存放布尔值个数
	 */
	private static final int TWOBYTEBOOL = 16;

	/**
	 * 16个bit 构成16个布尔
	 */
	boolean[] values = new boolean[16];

	@Override
	public BooleanModbusDataInRegister decode(byte[] bytes, int offset) {
		return getValFormByte(bytes[BYTEINREGISTER * offset], bytes[BYTEINREGISTER * offset + 1]);
	}

	@Override
	public BooleanModbusDataInRegister decode(ByteBuffer byteBuf) {
		return getValFormByte(byteBuf.get(), byteBuf.get());

	}

	@Override
	public IModbusDataType encode(List<Byte> bytes) {
		bytes.add(getByteFormValue(0));
		bytes.add(getByteFormValue(1));
		return this;
	}

	private BooleanModbusDataInRegister getValFormByte(byte b1, byte b2) {
		for (int i = 0; i < ONEBYTEBOOL; i++) {
			this.values[i] = (byte) ((b1 >> i) & 0x01) == 0x01;
		}
		for (int i = ONEBYTEBOOL; i < TWOBYTEBOOL; i++) {
			this.values[i] = (byte) ((b2 >> (i - 8)) & 0x01) == 0x01;
		}
		return this;
	}

	/**
	 * 入参0  即获取高位的byte
	 * 入参1 即获取低位的byte
	 *
	 * @param whichByte
	 * @return
	 */
	private byte getByteFormValue(int whichByte) {
		byte b = 0;
		for (int i = whichByte * ONEBYTEBOOL; i < (whichByte + 1) * ONEBYTEBOOL; i++) {
			if (this.values[i]) {
				b |= (0x01 << i);
			}
		}
		return b;
	}

	public boolean getValue(int index) {
		if (index < values.length) {
			return this.values[index];
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void setValue(int index, boolean value) {
		if (index < values.length) {
			this.values[index] = value;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean[] getValues() {
		return this.values;
	}

	@Override
	public List<Register> getRegisters() {
		List<Register> registers = new ArrayList<>();
		registers.add(new Register(getByteFormValue(0), getByteFormValue(1)));
		return registers;
	}
}
