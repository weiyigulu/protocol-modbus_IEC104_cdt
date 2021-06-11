package wei.yigulu.modbus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.domain.response.TcpModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusResponseDataUtils;
import wei.yigulu.utils.DataConvertor;

import java.net.InetSocketAddress;

/**
 * 以TCPserver的通讯方式  传输RTU协议  Master角色 处理器
 * @author: xiuwei
 * @version:
 */
public class ModbusRtuMasterWithTcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	protected Logger log;

	/**
	 * Slave 104 handle
	 *
	 * @param builder slaver builder
	 */
	public ModbusRtuMasterWithTcpServerHandler(ModbusRtuMasterWithTcpServer builder) {
		this.builder = builder;
		this.log = builder.getLog();
	}

	protected ModbusRtuMasterWithTcpServer builder;


	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		//收数据
		log.debug("re <== " + DataConvertor.ByteBuf2String(byteBuf));
		((ModbusMasterBuilderInterface) this.builder).getOrCreateSynchronousWaitingRoom().setData(byteBuf.nioBuffer());
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("ModbusSlave交互时发生异常", cause);
	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = ipSocket.getAddress().getHostAddress();
		Integer clientPort = ipSocket.getPort();
		if (!this.builder.getConnectFilterManager().verdict(ctx.channel())) {
			ctx.channel().close();
			log.info(clientIp + ":" + clientPort + "客户端被过滤链拦截，已关闭通道");
			return;
		}
		log.info(clientIp + ":" + clientPort + "客户端连接");
		this.builder.connected(ipSocket);
		this.builder.getChannels().add(ctx.channel());
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = ipSocket.getAddress().getHostAddress();
		Integer clientPort = ipSocket.getPort();
		log.info(clientIp + ":" + clientPort + "客户端断开连接");
		this.builder.getChannels().remove(ctx.channel());
		this.builder.disconnected(ipSocket);
	}
}
