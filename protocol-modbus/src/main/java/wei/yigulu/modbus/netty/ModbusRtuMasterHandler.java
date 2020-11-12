package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.utils.DataConvertor;

import java.util.concurrent.TimeUnit;

/**
 * 16进制报文解析工具
 *
 * @author xiuwei
 */
@Data
@Slf4j
public class ModbusRtuMasterHandler extends SimpleChannelInboundHandler<ByteBuf> {


	private static final int MINLEN = 5;

	private ModbusRtuMasterBuilder modbusMaster;

	public ModbusRtuMasterHandler(ModbusRtuMasterBuilder modbusMaster) {
		this.modbusMaster = modbusMaster;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		modbusMaster.getLog().info("-----连接串口{}成功-----", this.modbusMaster.getCommPortId());
	}

	/**
	 * channel断连及不稳定时调用的方法
	 *
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		modbusMaster.getLog().error("串口{}连接中断,正在启动重连机制... ", this.modbusMaster.getCommPortId());
		//在客户端与服务端连接过程中如果断连，就会调用的方法
		final EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(() -> {
			modbusMaster.getLog().info("正在重连串口{}", this.modbusMaster.getCommPortId());
			this.modbusMaster.create();
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
		modbusMaster.getLog().error("串口异常消息:{}", cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		modbusMaster.getLog().info("接收到串口{}发来数据帧: <= " + DataConvertor.ByteBuf2String(msg), this.modbusMaster.getCommPortId());
		if (msg.readableBytes() >= MINLEN) {
			this.modbusMaster.getOrCreateSynchronousWaitingRoom().setData(msg.nioBuffer());
		}
	}
}
