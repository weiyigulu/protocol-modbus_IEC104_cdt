package wei.yigulu.cdt.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Data;
import org.slf4j.Logger;
import wei.yigulu.cdt.cdtframe.CDTFrameBean;
import wei.yigulu.utils.DataConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 16进制报文解析工具
 *
 * @author xiuwei
 */
@Data
public class SlaverHandler extends SimpleChannelInboundHandler<ByteBuf> {
	CDTSlaver cdtSlaver;
	List<ScheduledFuture> scheduledFutures;
	private Logger log;

	public SlaverHandler(CDTSlaver cdtSlaver) {
		this.cdtSlaver = cdtSlaver;
		this.log = cdtSlaver.getLog();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("-----连接串口{}成功-----", this.cdtSlaver.getCommPortId());
		this.cdtSlaver.getDataTransmitter().connected();
		this.scheduledFutures = new ArrayList<>();
		scheduledFutures.add(ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
			write(cdtSlaver.getDataTransmitter().transmitImportantYc(), ctx);
		}, 0, 3, TimeUnit.SECONDS));
		//次要遥测 6秒一次
		scheduledFutures.add(ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
			write(cdtSlaver.getDataTransmitter().transmitSecondYc(), ctx);
		}, 0, 6, TimeUnit.SECONDS));
		//一般遥测20秒一次
		scheduledFutures.add(ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
			write(cdtSlaver.getDataTransmitter().transmitCommonYc(), ctx);
		}, 0, 20, TimeUnit.SECONDS));
		//遥信 量 间隙发送
		scheduledFutures.add(ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
			write(cdtSlaver.getDataTransmitter().transmitYx(), ctx);
		}, 0, 10, TimeUnit.SECONDS));
	}

	/**
	 * 频道不活跃
	 * channel断连及不稳定时调用的方法
	 *
	 * @param ctx 通道对象
	 * @throws Exception 异常
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		for (ScheduledFuture scheduledFuture : this.scheduledFutures) {
			if (!scheduledFuture.isCancelled()) {
				scheduledFuture.cancel(true);
			}
		}
		this.cdtSlaver.getDataTransmitter().disconnected();
		log.error("串口{}连接中断,正在启动重连机制... ", this.cdtSlaver.getCommPortId());
		//在客户端与服务端连接过程中如果断连，就会调用的方法
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule((Callable) () -> {
			log.info("正在重连串口{}", cdtSlaver.getCommPortId());
			cdtSlaver.create();
			return null;
		}, 3L, TimeUnit.SECONDS);

	}

	/**
	 * channel连接及传输报错时调用的方法
	 *
	 * @param ctx   通道对象
	 * @param cause 异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		log.error("串口异常消息:{}", cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		log.info("接收到串口{}发来数据帧:" + DataConvertor.ByteBuf2String(msg), this.cdtSlaver.getCommPortId());
	}


	private void write(List<CDTFrameBean> list, ChannelHandlerContext ctx) {
		try {
			if (list != null && list.size() != 0) {
				for (CDTFrameBean c : list) {
					byte[] bb = c.encode();
					log.info("向串口{}发送数据帧:" + DataConvertor.Byte2String(bb), this.cdtSlaver.getCommPortId());
					ctx.writeAndFlush(Unpooled.copiedBuffer(bb));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
