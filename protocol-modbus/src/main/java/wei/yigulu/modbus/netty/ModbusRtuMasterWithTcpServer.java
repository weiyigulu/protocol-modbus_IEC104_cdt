package wei.yigulu.modbus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import wei.yigulu.modbus.domain.synchronouswaitingroom.RtuSynchronousWaitingRoom;
import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.netty.AbstractTcpServerBuilder;
import wei.yigulu.netty.AbstractTcpSlaverBuilder;
import wei.yigulu.netty.MasterInterface;
import wei.yigulu.netty.ProtocolChannelInitializer;
import wei.yigulu.utils.DataConvertor;

/**
 * 以TCPserver的通讯方式  传输RTU协议  Master角色
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusRtuMasterWithTcpServer  extends AbstractTcpServerBuilder implements ModbusMasterBuilderInterface, MasterInterface {

	private SynchronousWaitingRoom synchronousWaitingRoom;

	public ModbusRtuMasterWithTcpServer(int port) {
		super(port);
	}

	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		return new ProtocolChannelInitializer(this) {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ModbusRtuMasterDelimiterHandler().setLog(getLog()));
				ch.pipeline().addLast(new ModbusRtuMasterWithTcpServerHandler((ModbusRtuMasterWithTcpServer) builder));
			}
		};
	}

	@Override
	public SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() throws ModbusException {
		if (this.synchronousWaitingRoom == null) {
			this.synchronousWaitingRoom = new RtuSynchronousWaitingRoom();
		}
		return this.synchronousWaitingRoom;
	}

	@Override
	public void sendFrameToOpposite(byte[] bytes) {
		if(getChannels().size()>0){
			getChannels().forEach(c->{
				getLog().info("se ==> "+c.remoteAddress()+" ：" + DataConvertor.Byte2String(bytes));
				c.writeAndFlush(Unpooled.copiedBuffer(bytes));
			});
		}else{
			throw new RuntimeException("无客户端连接");
		}
	}

	@Override
	public void sendFrameToOpposite(ByteBuf byteBuf) {
		if(getChannels().size()>0){
			getChannels().forEach(c->{
				getLog().info("se ==> "+c.remoteAddress()+" ：" + DataConvertor.ByteBuf2String(byteBuf));
				c.writeAndFlush(Unpooled.copiedBuffer(byteBuf));
			});
		}else{
			throw new RuntimeException("无客户端连接");
		}
	}
}
