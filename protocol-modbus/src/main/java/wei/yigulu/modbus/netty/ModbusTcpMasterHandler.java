package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import wei.yigulu.netty.AbstractTcpMasterBuilder;
import wei.yigulu.utils.DataConvertor;

import java.net.InetSocketAddress;

/**
 * modbus 通讯所使用的处理逻辑
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusTcpMasterHandler extends SimpleChannelInboundHandler<ByteBuf> {

	protected Logger log;
	protected AbstractTcpMasterBuilder masterBuilder;
	protected int exceptionNum;
	/**
	 * 是否是主动断开连接
	 */
	protected boolean isInitiative;

	/**
	 * Master 104 handle
	 *
	 * @param masterBuilder master builder
	 */
	public ModbusTcpMasterHandler(AbstractTcpMasterBuilder masterBuilder) {
		this.masterBuilder = masterBuilder;
		this.log = masterBuilder.getLog();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		//收数据
		log.debug("re <== " + DataConvertor.ByteBuf2String(byteBuf));
		((ModbusMasterBuilderInterface) this.masterBuilder).getOrCreateSynchronousWaitingRoom().setData(byteBuf.nioBuffer());
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
		this.isInitiative = false;
		InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = ipSocket.getAddress().getHostAddress();
		Integer clientPort = ipSocket.getPort();
		log.info("连接" + clientIp + ":" + clientPort + "服务端成功");
		this.masterBuilder.connected();
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
		ctx.close();
		if (masterBuilder != null) {
			this.exceptionNum = 0;
			this.isInitiative = false;
			log.error(masterBuilder.getIp() + ":" + masterBuilder.getPort() + "断线,尝试重连");
			masterBuilder.getOrCreateConnectionListener().operationComplete(masterBuilder.getFuture());
		}
	}
}
