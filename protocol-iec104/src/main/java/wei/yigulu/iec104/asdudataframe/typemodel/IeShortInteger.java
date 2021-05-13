package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.exception.Iec104Exception;

import java.util.List;

/**
 * 104短整型数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IeShortInteger implements IecDataInterface {

	public static final int OCCUPYBYTES = 2;

	private Integer value;

	/**
	 * Ie short integer
	 *
	 * @param is is
	 */
	public IeShortInteger(ByteBuf is) throws Iec104Exception {
		if (is.readableBytes() < OCCUPYBYTES) {
			throw new Iec104Exception(3301, "可用字节不足，不能进行读取");
		}
		value = ((short) ((is.readByte() & 0xff) | ((is.readByte() & 0xff) << 8))) + 0;
	}

	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		int tempVal = value;
		buffer.add((byte) tempVal);
		buffer.add((byte) (tempVal >> 8));
	}

	@Override
	public Integer getIecValue() {
		return this.value;
	}
}
