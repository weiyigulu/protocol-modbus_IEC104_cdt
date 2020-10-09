package wei.yigulu.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public abstract class AbstractDelimiterHandler extends ChannelInboundHandlerAdapter {

	@Setter
	@Accessors(chain = true)
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	protected ByteBuf cumulation;

	protected DateTime timeMark = DateTime.now();

	protected static ByteBuf expandCumulation(ByteBuf byteBuf1, ByteBuf byteBuf2) {
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
	public abstract void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;


}
