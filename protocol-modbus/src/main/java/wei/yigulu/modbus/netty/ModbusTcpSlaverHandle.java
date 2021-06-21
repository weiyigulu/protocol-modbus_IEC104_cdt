package wei.yigulu.modbus.netty;


import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import wei.yigulu.modbus.domain.command.AbstractModbusCommand;
import wei.yigulu.modbus.domain.command.RtuModbusCommand;
import wei.yigulu.modbus.domain.command.TcpModbusCommand;
import wei.yigulu.modbus.domain.confirm.TcpModbusConfirm;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.UnknownTypeRegisterValue;
import wei.yigulu.modbus.domain.request.AbstractModbusRequest;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.domain.response.RtuModbusResponse;
import wei.yigulu.modbus.domain.response.TcpModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusResponseDataUtils;
import wei.yigulu.utils.DataConvertor;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
		byte[] bbs = new byte[0];
		if (AbstractModbusRequest.FUNCTION_CODES.contains(byteBuf.getByte(7))) {
			TcpModbusRequest request = new TcpModbusRequest().decode(byteBuf.nioBuffer());
			TcpModbusResponse response = new TcpModbusResponse();
			response.setTcpExtraCode(request.getTcpExtraCode());
			try {
				bbs = ModbusResponseDataUtils.buildResponse(this.slaverBuilder.getModbusSlaveDataContainer(), request, response);
			} catch (ModbusException e) {
				log.error(e.getMsg());
			}
		}else if (AbstractModbusCommand.FUNCTION_CODES.contains(byteBuf.getByte(7))) {
			TcpModbusCommand command = new TcpModbusCommand().decode(byteBuf.nioBuffer());
			TcpModbusConfirm confirm = new TcpModbusConfirm().setTransactionIdentifier(command.getTcpExtraCode().getTransactionIdentifier());
			try {
			bbs=slaverBuilder.receiveCommandAndAnswer(command,confirm);
			} catch (ModbusException e) {
				log.error(e.getMsg());
			}
		}
		if (bbs.length>0) {
			ctx.writeAndFlush(Unpooled.copiedBuffer(bbs));
			log.debug("se =>" + DataConvertor.Byte2String(bbs));
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

