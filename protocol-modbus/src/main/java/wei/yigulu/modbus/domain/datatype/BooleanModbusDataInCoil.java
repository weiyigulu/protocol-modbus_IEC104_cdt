package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 8个布尔值组成的数据类型
 * 仅对应 01 和 02 功能码
 *
 * @author: xiuwei
 * @version:
 */
public class BooleanModbusDataInCoil extends CoilValue {

	/**
	 * 八个布尔 对应一字节 八比特 从
	 */
	boolean[] values = new boolean[8];


	@Override
	public IModbusDataType decode(byte[] bytes, int offset) {
		return getValFormByte(bytes[2 * offset]);
	}

	@Override
	public IModbusDataType decode(ByteBuffer byteBuf) {
		return getValFormByte(byteBuf.get());

	}

	private IModbusDataType getValFormByte(byte b) {
		for (int i = 0; i < values.length; i++) {
			this.values[i] = (byte) ((b >> i) & 0x01) == 0x01;
		}
		return this;
	}

	@Override
	public IModbusDataType encode(List<Byte> bytes) {
		byte b = 0;
		for (int i = 0; i < values.length; i++) {
			if (this.values[i]) {
				b |= (0x01 << i);
			}
		}
		bytes.add(b);
		return this;
	}
}
