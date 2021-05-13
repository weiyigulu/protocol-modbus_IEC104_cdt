package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.exception.Iec104Exception;

import java.util.List;

/**
 * 应用数据单元类型的基类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformationBodyAddress {

	public static final int OCCUPYBYTES = 3;

	/**
	 * 信息体地址
	 * 共三位16进制字节
	 * 高低位逆转
	 */
	protected int address;

	/**
	 * Information body address
	 *
	 * @param b1 b 1
	 * @param b2 b 2
	 * @param b3 b 3
	 */
	public InformationBodyAddress(byte b1, byte b2, byte b3) {
		this.address = (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16);
	}


	/**
	 * Information body address
	 *
	 * @param is is
	 */
	public InformationBodyAddress(ByteBuf is) throws Iec104Exception {
		if (is.readableBytes() < OCCUPYBYTES) {
			throw new Iec104Exception(3301, "可用字节不足，不能进行读取");
		}
		this.address = (is.readByte() & 0xff) | ((is.readByte() & 0xff) << 8) | ((is.readByte() & 0xff) << 16);
	}

	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		buffer.add((byte) address);
		buffer.add((byte) (address >> 8));
		buffer.add((byte) (address >> 16));
	}

	@Override
	public String toString() {
		return "信息体地址为:" + address + ";";
	}

}
