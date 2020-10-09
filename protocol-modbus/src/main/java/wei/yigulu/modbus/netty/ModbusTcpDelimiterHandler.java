package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.utils.DataConvertor;


/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusTcpDelimiterHandler extends ChannelInboundHandlerAdapter {

	public static int maxLength = 256;
	@Setter
	@Accessors(chain = true)
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ByteBuf cumulation;

	private DateTime timeMark = DateTime.now();

	private static ByteBuf expandCumulation(ByteBuf byteBuf1, ByteBuf byteBuf2) {
		ByteBuf oldCumulation = byteBuf1;
		byteBuf1 = byteBuf1.alloc().buffer(oldCumulation.readableBytes() + byteBuf2.readableBytes());
		byteBuf1.writeBytes(oldCumulation);
		byteBuf1.writeBytes(byteBuf2);
		byteBuf1.readerIndex(0);
		while (!oldCumulation.release()) {
		}
		while (!byteBuf2.release()) {
		}
		return byteBuf1;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//log.warn("接收到原始的报文 ："+ DataConvertor.ByteBuf2String((ByteBuf) msg));
		if (cumulation == null) {
			cumulation = (ByteBuf) msg;
			if (cumulation.readableBytes() > maxLength) {
				while (!cumulation.release()) {
				}
				cumulation = null;
				log.warn("报文超长舍弃");
				return;
			}
		} else {
			if (timeMark.plusMillis(50).isBeforeNow()) {
				log.warn("上一帧数据长度不足，但两帧时间间隔较长上一帧被舍弃 舍弃的数据帧为：" + DataConvertor.ByteBuf2String(cumulation));
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
		cumulation.readBytes(4);
		int length = cumulation.readShort();
		if (length > 255 && length < 3) {
			log.warn("不是正常的长度，该帧疑似异常帧，舍弃");
			while (!cumulation.release()) {
			}
			cumulation = null;
			return;
		}
		if (length > cumulation.readableBytes()) {
			log.debug("数据帧长度不足进入等待 预计长度：" + length + ",实际长度：" + cumulation.readableBytes());
			timeMark = DateTime.now();
			cumulation.readerIndex(0);
			return;
		} else if (length == cumulation.readableBytes()) {
			cumulation.readerIndex(0);
			ctx.fireChannelRead(cumulation);
			while (!cumulation.release()) {
			}
			cumulation = null;
		} else {
			cumulation.readerIndex(0);
			ctx.fireChannelRead(cumulation.readBytes(length));
			byte[] bbs = new byte[cumulation.readableBytes()];
			while (!cumulation.release()) {
			}
			cumulation = Unpooled.copiedBuffer(bbs);
			return;
		}

	}


}
