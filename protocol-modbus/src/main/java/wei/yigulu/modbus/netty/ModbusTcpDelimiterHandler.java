package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import wei.yigulu.netty.AbstractDelimiterHandler;


/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusTcpDelimiterHandler extends AbstractDelimiterHandler {

	/**
	 * 最短帧为错误帧 9为   4位事务+2位长度+1位slaveID+1位functionCode+1位exceptionCode
	 */
	private static final int MINLENGTH = 9;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//log.warn("接收到原始的报文 ："+ DataConvertor.ByteBuf2String((ByteBuf) msg));
		if (isOverMaxLength((ByteBuf) msg)) {
			return;
		}
		//数据帧长度不足 记录时间 等待下一帧进入
		int length;
		while (cumulation.readableBytes() >= MINLENGTH) {
			cumulation.markReaderIndex();
			//去掉四位事务帧
			cumulation.readBytes(4).release();
			length = cumulation.readUnsignedShort();
			if (length > 255 || length < 3) {
				log.warn("不是正常的长度，该帧疑似异常帧，舍弃");
				clearCumulation();
				return;
			}
			if (length > cumulation.readableBytes()) {
				log.debug("数据帧长度不足进入等待 预计长度：" + length + ",实际长度：" + cumulation.readableBytes());
				timeMark = DateTime.now();
				cumulation.resetReaderIndex();
				return;
			} else {
				cumulation.resetReaderIndex();
				ctx.fireChannelRead(cumulation.readBytes(length + 6));
			}
		}
		if (cumulation.readableBytes() != 0) {
			this.timeMark = DateTime.now();
		} else {
			clearCumulation();
		}
	}


}
