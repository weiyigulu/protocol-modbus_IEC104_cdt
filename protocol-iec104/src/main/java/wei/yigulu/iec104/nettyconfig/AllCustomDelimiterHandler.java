package wei.yigulu.iec104.nettyconfig;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import wei.yigulu.netty.AbstractDelimiterHandler;
import wei.yigulu.utils.DataConvertor;


/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class AllCustomDelimiterHandler extends AbstractDelimiterHandler {


	private static final byte[] HEAD = new byte[]{0x68};


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (cumulation == null) {
			cumulation = (ByteBuf) msg;
			if (cumulation.readableBytes() > 10240) {
				while (!cumulation.release()) {
				}
				cumulation = null;
				log.warn("报文超长舍弃");
				return;
			}
		} else {
			if (timeMark.plusMillis(10).isBeforeNow()) {
				log.warn("上一帧数据长度不足，但两帧时间间隔较长上一帧被舍弃");
				while (!cumulation.release()) {
				}
				cumulation = (ByteBuf) msg;
			} else {
				//拓展寄居buffer
				cumulation = expandCumulation(cumulation, (ByteBuf) msg);
			}
		}
		//数据帧长度不足 记录时间 等待下一帧进入
		if (cumulation.readableBytes() < 6) {
			timeMark = DateTime.now();
			return;
		}
		int len;
		//查看第一个 HEAD 的头位置
		int headIndex = getHeadIndex(0, cumulation.writerIndex(), cumulation);
		//当数据帧里存在头字节 且长度大于3时进入循环
		while (cumulation.readableBytes() >= 6 && headIndex != -1) {
			//如果头字节不在第一个字节 那么读取标志向后推到头字节位置
			if (headIndex > cumulation.readerIndex()) {
				log.warn("舍弃了一无用段报文:" + DataConvertor.ByteBuf2String(cumulation.readBytes(headIndex - cumulation.readerIndex())));
			}
			//标记读取位置
			cumulation.markReaderIndex();
			//向后读取一位 即0x68的占位
			cumulation.readBytes(1);
			//获取到该帧的长度 帧内标定的长度
			len = cumulation.readByte() & 0xff;
			//如果帧的真实长度少于 帧内标定长度则代表数据帧不完整，退出循环等待下一数据帧进入进行粘帧
			if (cumulation.readableBytes() < len) {
				cumulation.resetReaderIndex();
				//数据帧长度不足 记录时间
				timeMark = DateTime.now();
				return;
			} else {
				cumulation.resetReaderIndex();
				//如果数据帧长度足够 将规定长度的直接加入out 队列
				ctx.fireChannelRead(cumulation.readBytes(len + 2));
				//查看后续的字节里面头字节的位置
				headIndex = getHeadIndex(cumulation.readerIndex(), cumulation.writerIndex(), cumulation);
			}
		}
		if (cumulation.readableBytes() != 0 && headIndex >= cumulation.readerIndex()) {
			//buffer中还有数据 而且其中有数据头
			timeMark = DateTime.now();
			return;
		} else {
			//buffer没有数据 或剩余这段字节中没有数据头
			if (cumulation.readableBytes() != 0) {
				log.warn("这段字节中没有数据头,舍弃:" + DataConvertor.ByteBuf2String(cumulation.readBytes(cumulation.readableBytes())));
			}
			while (!cumulation.release()) {
			}
			cumulation = null;
		}
	}


	private int getHeadIndex(int from, int end, ByteBuf byteBuf) {
		if (byteBuf.readableBytes() < HEAD.length) {
			return -1;
		}
		for (int i = from; i < end; i++) {
			if (isEqualByteArr(HEAD, ByteBufUtil.getBytes(byteBuf, i, HEAD.length))) {
				return i;
			}
		}
		return -1;
	}

	private boolean isEqualByteArr(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			return false;
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}


}
