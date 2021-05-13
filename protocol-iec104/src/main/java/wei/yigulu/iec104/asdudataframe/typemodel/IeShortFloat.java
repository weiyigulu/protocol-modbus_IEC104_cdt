package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.exception.Iec104Exception;

import java.util.List;

/**
 * 短浮点值
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IeShortFloat implements IecDataInterface {

	public static final int OCCUPYBYTES = 4;

	private float value;

	/**
	 * Ie short float
	 *
	 * @param is is
	 */
	public IeShortFloat(ByteBuf is) throws Iec104Exception {
		if (is.readableBytes() < OCCUPYBYTES) {
			throw new Iec104Exception(3301, "可用字节不足，不能进行读取");
		}
		value = Float.intBitsToFloat((is.readByte() & 0xff) | ((is.readByte() & 0xff) << 8)
				| ((is.readByte() & 0xff) << 16) | ((is.readByte() & 0xff) << 24));
	}


	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		int tempVal = Float.floatToIntBits(value);
		buffer.add((byte) tempVal);
		buffer.add((byte) (tempVal >> 8));
		buffer.add((byte) (tempVal >> 16));
		buffer.add((byte) (tempVal >> 24));
	}


	@Override
	public String toString() {
		return "短浮点数值: " + value;
	}

	@Override
	public Float getIecValue() {
		return this.value;
	}
}
