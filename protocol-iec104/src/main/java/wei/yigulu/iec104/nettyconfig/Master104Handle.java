package wei.yigulu.iec104.nettyconfig;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.container.Iec104Link;
import wei.yigulu.iec104.container.LinkContainer;
import wei.yigulu.netty.AbstractTcpMasterBuilder;
import wei.yigulu.utils.DataConvertor;

import java.net.InetSocketAddress;

/**
 * 消息处理类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

@NoArgsConstructor
public class Master104Handle extends SimpleChannelInboundHandler<ByteBuf> {

	private Logger log;

	/**
	 * Master 104 handle
	 *
	 * @param masterBuilder master builder
	 */
	public Master104Handle(AbstractTcpMasterBuilder masterBuilder) {
		this.masterBuilder = masterBuilder;
		this.log = masterBuilder.getLog();
	}

	public Master104Handle(AbstractTcpMasterBuilder masterBuilder, Class<? extends Apdu> apduClass) {
		this(masterBuilder);
		this.apduClass = apduClass;
	}

	private AbstractTcpMasterBuilder masterBuilder;

	private int testNum;

	private int exceptionNum;

	/**
	 * 是否是主动断开连接
	 */
	private boolean isInitiative;

	private Class<? extends Apdu> apduClass = Apdu.class;


	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		//收数据
		log.debug("----------------------------------------------------------------------------------");
		log.debug("re <= "+DataConvertor.ByteBuf2String(msg));
		Apdu apdu = apduClass.newInstance().setChannel(ctx.channel()).setIec104Builder(masterBuilder).setLog(log).loadByteBuf(msg);
		if (apdu.getApciType() == Apdu.ApciType.I_FORMAT) {
			this.testNum = 0;
		}
		apdu.answer();

	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (this.exceptionNum > 10) {
			this.isInitiative = true;
			reconnect(ctx);
		}
		this.exceptionNum++;
		ctx.flush();
		cause.printStackTrace();
		log.error("发生{}次异常，异常内容{}", this.exceptionNum, cause.getLocalizedMessage());

	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("发出U帧，启动命令");
		this.isInitiative = false;
		InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = ipSocket.getAddress().getHostAddress();
		Integer clientPort = ipSocket.getPort();
		log.info("连接" + clientIp + ":" + clientPort + "服务端成功");
		LinkContainer.getInstance().getLinks().put(ctx.channel().id(), new Iec104Link(ctx.channel(), clientIp, clientPort, Iec104Link.Role.SLAVER,masterBuilder.getLog()));
		ctx.writeAndFlush(Unpooled.copiedBuffer(TechnicalTerm.START));
		this.masterBuilder.connected();
	}


	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		log.debug("第{}次发送测试帧：" + DataConvertor.Byte2String(TechnicalTerm.TEST), testNum);
		ctx.writeAndFlush(Unpooled.copiedBuffer(TechnicalTerm.TEST));
		IdleStateEvent i = (IdleStateEvent) evt;
		if (!i.isFirst()) {
			log.debug("链路长时间无响应，重新连接");
			this.isInitiative = true;
			reconnect(ctx);
		}
		if (this.testNum >= 5) {
			log.debug("链路长时间无数据传输，重新连接");
			this.isInitiative = true;
			reconnect(ctx);
		}
		this.testNum++;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (isInitiative) {
			this.isInitiative = false;
		} else {
			reconnect(ctx);
		}
		this.masterBuilder.disconnected();

	}


	private synchronized void reconnect(ChannelHandlerContext ctx) throws Exception {
		LinkContainer.getInstance().getLinks().remove(ctx.channel().id());
		ctx.close();
		if (masterBuilder != null) {
			this.testNum = 0;
			this.exceptionNum = 0;
			this.isInitiative = false;
			log.error(masterBuilder.getIp() + ":" + masterBuilder.getPort() + "断线,尝试重连");
			masterBuilder.getOrCreateConnectionListener().operationComplete(masterBuilder.getFuture());
		}
	}
}

