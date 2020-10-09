package wei.yigulu.cdt.cdtframe;

import io.netty.buffer.ByteBuf;
import wei.yigulu.utils.CrcUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 * cdt数据类型的基类
 *
 * @author 修唯xiuwei
 **/
public abstract class BaseDateType<T> {

	protected int functionNum;

	Map<Integer, T> dates;

	protected int crc;

	public int getCrc() {
		return crc;
	}

	public int getFunctionNum() {
		return functionNum;
	}

	public void loadBytes(ByteBuf byteBuf) {
		byte[] bs = new byte[5];
		if (byteBuf.readableBytes() > 5) {
			byteBuf.readBytes(bs);
			this.functionNum = bs[0] & 0xff;
			this.crc = byteBuf.readByte();
			if ((this.crc & 0xff) == CrcUtils.generateCRC8(bs)) {
				readDates(Arrays.copyOfRange(bs, 1, bs.length));
			}
		}
	}

	/**
	 * 各子类根据自身的属性 进行赋值
	 *
	 * @param bs 字节数组
	 */
	public abstract void readDates(byte[] bs);


	/**
	 * 获取到帧内数据
	 *
	 * @return 数据点位   数据
	 */
	public abstract Map<Integer, T> getDates();


	/**
	 * 对数据库进行编码
	 *
	 * @param byteBuffer 字节缓冲串
	 */
	protected abstract void encode(ByteBuffer byteBuffer);


}
