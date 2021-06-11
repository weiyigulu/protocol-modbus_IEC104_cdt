package wei.yigulu.utils;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

import java.util.Formatter;

/**
 * 对支付串的处理工具
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class DataConvertor {

	/**
	 * 字节数字转16进制字符串
	 *
	 * @param ba ba
	 * @return string
	 */
	public static String Byte2String(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}
		Formatter f = new Formatter();
		for (int i = 0; i < ba.length; ++i) {
			f.format("%02x ", ba[i]);
		}
		return f.toString();
	}


	/**
	 * Byte append string
	 *
	 * @param bytes bytes
	 * @return the string
	 */
	public static String byteAppend(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = bytes.length - 1; i >= 0; i--) {
			stringBuffer.append(String.format("%02d", bytes[i]));
		}
		return stringBuffer.toString();
	}

	/**
	 * 字节数缓冲区字转16进制字符串
	 *
	 * @param buf buf
	 * @return string
	 */
	public static String ByteBuf2String(ByteBuf buf) {
		if (!buf.isReadable()) {
			return null;
		}
		ByteBuf b1 = buf.copy();
		byte[] bs = new byte[b1.readableBytes()];
		b1.readBytes(bs);
		ReferenceCountUtil.release(b1);
		return Byte2String(bs);
	}


	/**
	 * 字节数缓冲区字转16进制字符串 并解除指向
	 *
	 * @param buf buf
	 * @return string
	 */
	public static String ByteBuf2StringAndRelease(ByteBuf buf) {
		String s = ByteBuf2String(buf);
		ReferenceCountUtil.release(buf);
		return s;

	}

}
