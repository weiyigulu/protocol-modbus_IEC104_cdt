package wei.yigulu.cdt.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.slf4j.Logger;
import wei.yigulu.cdt.cdtframe.CDTFrameBean;
import wei.yigulu.utils.DataConvertor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 16进制报文解析工具
 *
 * @author xiuwei
 */
@Data
public class MasterHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final int MINLEN = 6;
	private Logger log;
	private CDTMaster cdtMaster;

	public MasterHandler(CDTMaster cdtMaster) {
		this.cdtMaster = cdtMaster;
		this.log = cdtMaster.getLog();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("-----连接串口{}成功-----", this.cdtMaster.getCommPortId());
		this.cdtMaster.getDataHandler().connected();
	}

	/**
	 * channel断连及不稳定时调用的方法
	 *
	 * @param ctx 通道对象
	 * @throws Exception 异常
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.error("串口{}连接中断,正在启动重连机制... ", this.cdtMaster.getCommPortId());
		//在客户端与服务端连接过程中如果断连，就会调用的方法
		this.cdtMaster.getDataHandler().disconnected();
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule((Callable) () -> {
			log.info("正在重连串口{}", cdtMaster.getCommPortId());
			cdtMaster.create();
			return null;
		}, 3L, TimeUnit.SECONDS);
	}

	/**
	 * channel连接及传输报错时调用的方法
	 *
	 * @param ctx   通道上下文
	 * @param cause 异常对象
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		log.error(cause.getMessage());
		log.error("串口异常消息:{}", cause.getMessage());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		log.info("接收到串口{}发来数据帧:" + DataConvertor.ByteBuf2String(msg), this.cdtMaster.getCommPortId());
		if (msg.readableBytes() > MINLEN) {
			CDTFrameBean cdtFrameBean = new CDTFrameBean(msg);
			log.info(cdtFrameBean.toString());
			this.cdtMaster.getDataHandler().processFrame(cdtFrameBean);
		}
	}
}
