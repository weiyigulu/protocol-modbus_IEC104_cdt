package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.command.AbstractModbusCommand;
import wei.yigulu.modbus.domain.command.RtuModbusCommand;
import wei.yigulu.modbus.domain.confirm.RtuModbusConfirm;
import wei.yigulu.modbus.domain.request.AbstractModbusRequest;
import wei.yigulu.modbus.domain.request.RtuModbusRequest;
import wei.yigulu.modbus.domain.response.RtuModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusResponseDataUtils;
import wei.yigulu.utils.DataConvertor;

import java.util.concurrent.TimeUnit;

/**
 * 16进制报文解析工具
 *
 * @author xiuwei
 */
@Data
@Slf4j
public class ModbusRtuSlaverHandler extends SimpleChannelInboundHandler<ByteBuf> {


	private static final int MINLEN = 6;

	private ModbusRtuSlaverBuilder modbusSlaver;

	public ModbusRtuSlaverHandler(ModbusRtuSlaverBuilder modbusSlaver) {
		this.modbusSlaver = modbusSlaver;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		modbusSlaver.getLog().info("-----连接串口{}成功-----", this.modbusSlaver.getCommPortId());
	}

	/**
	 * channel断连及不稳定时调用的方法
	 *
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		modbusSlaver.getLog().error("串口{}连接中断,正在启动重连机制... ", this.modbusSlaver.getCommPortId());
		//在客户端与服务端连接过程中如果断连，就会调用的方法
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(() -> {
			modbusSlaver.getLog().info("正在重连串口{}", this.modbusSlaver.getCommPortId());
			this.modbusSlaver.create();
		}, 3L, TimeUnit.SECONDS);
	}

	/**
	 * channel连接及传输报错时调用的方法
	 *
	 * @param ctx
	 * @param cause
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		modbusSlaver.getLog().error(cause.getMessage());
		modbusSlaver.getLog().error("串口异常消息:{}", cause.getMessage());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		modbusSlaver.getLog().debug("接收到串口{}发来数据帧: <= " + DataConvertor.ByteBuf2String(msg), this.modbusSlaver.getCommPortId());
		if (msg.readableBytes() > MINLEN) {
			byte[] bbs = new byte[0];
			if (AbstractModbusRequest.FUNCTION_CODES.contains(msg.getByte(1))) {
				RtuModbusRequest request = new RtuModbusRequest().decode(msg.nioBuffer());
				RtuModbusResponse response = new RtuModbusResponse();
				try {
					bbs = ModbusResponseDataUtils.buildResponse(this.modbusSlaver.getModbusSlaveDataContainer(), request, response);
				} catch (ModbusException e) {
					log.error(e.getMsg());
				}
			} else if (AbstractModbusCommand.FUNCTION_CODES.contains(msg.getByte(1))) {
				RtuModbusCommand command = new RtuModbusCommand().decode(msg.nioBuffer());
				RtuModbusConfirm confirm = new RtuModbusConfirm();
				try {
					bbs = modbusSlaver.receiveCommandAndAnswer(command, confirm);
				} catch (ModbusException e) {
					log.error(e.getMsg());
				}
			}
			if (bbs.length > 0) {
				ctx.writeAndFlush(Unpooled.copiedBuffer(bbs));
				modbusSlaver.getLog().debug("se =>" + DataConvertor.Byte2String(bbs));
			}
		}
	}
}
