package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 16个布尔值组成的数据类型
 * 仅对应 01 和 02 功能码
 *
 * @author: xiuwei
 * @version:
 */
public class BooleanModbusDataInRegister extends RegisterValue {

	/**
	 * 16个bit  构成16个布尔
	 */
	boolean[] values = new boolean[16];


	@Override
	public BooleanModbusDataInRegister decode(byte[] bytes, int offset) {
		return getValFormByte(bytes[2 * offset], bytes[2 * offset + 1]);
	}

	@Override
	public BooleanModbusDataInRegister decode(ByteBuffer byteBuf) {
		return getValFormByte(byteBuf.get(), byteBuf.get());

	}

	private BooleanModbusDataInRegister getValFormByte(byte b1, byte b2) {
		for (int i = 0; i < 8; i++) {
			this.values[i] = (byte) ((b1 >> i) & 0x01) == 0x01;
		}
		for (int i = 8; i < 16; i++) {
			this.values[i] = (byte) ((b2 >> (i - 8)) & 0x01) == 0x01;
		}
		return this;
	}

	@Override
	public BooleanModbusDataInRegister encode(List<Byte> bytes) {
		byte b = 0;
		for (int i = 0; i < 8; i++) {
			if (this.values[i]) {
				b |= (0x01 << i);
			}
		}
		bytes.add(b);
		b = 0;
		for (int i = 8; i < 16; i++) {
			if (this.values[i]) {
				b |= (0x01 << (i - 8));
			}
		}
		bytes.add(b);
		return this;
	}

	@Override
	public List<Register> getRegisters() {
		return null;
	}
}
