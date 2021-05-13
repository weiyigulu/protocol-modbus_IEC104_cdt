package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.domain.response.TcpModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusResponseDataUtils;
import wei.yigulu.utils.DataConvertor;

import java.net.InetSocketAddress;

/**
 * 消息处理类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@NoArgsConstructor
public class ModbusTcpSlaverHandle extends SimpleChannelInboundHandler<ByteBuf> {

	protected Logger log;

	/**
	 * Slave 104 handle
	 *
	 * @param slaverBuilder slaver builder
	 */
	public ModbusTcpSlaverHandle(ModbusTcpSlaverBuilder slaverBuilder) {
		this.slaverBuilder = slaverBuilder;
		this.log = slaverBuilder.getLog();
	}

	protected ModbusTcpSlaverBuilder slaverBuilder;


	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		//收数据
		log.debug("re <=" + DataConvertor.ByteBuf2String(byteBuf));
		TcpModbusRequest request = new TcpModbusRequest().decode(byteBuf.nioBuffer());
		TcpModbusResponse response = new TcpModbusResponse();
		response.setTcpExtraCode(request.getTcpExtraCode());
		try {
			byte[] bbs = ModbusResponseDataUtils.buildResponse(this.slaverBuilder.getModbusSlaveDataContainer(), request, response);
			ctx.writeAndFlush(Unpooled.copiedBuffer(bbs));
			log.debug("se =>" + DataConvertor.Byte2String(bbs));
		} catch (ModbusException e) {
			log.error(e.getMsg());
		}
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
		if (!this.slaverBuilder.getConnectFilterManager().verdict(ctx.channel())) {
			ctx.channel().close();
			log.info(clientIp + ":" + clientPort + "客户端被过滤链拦截，已关闭通道");
			return;
		}
		log.info(clientIp + ":" + clientPort + "客户端连接");
		this.slaverBuilder.connected(ipSocket);
		this.slaverBuilder.getChannels().add(ctx.channel());
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String clientIp = ipSocket.getAddress().getHostAddress();
		Integer clientPort = ipSocket.getPort();
		log.info(clientIp + ":" + clientPort + "客户端断开连接");
		this.slaverBuilder.getChannels().remove(ctx.channel());
		this.slaverBuilder.disconnected(ipSocket);
	}

}

