package wei.yigulu.modbus.netty;


import io.netty.channel.socket.SocketChannel;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.domain.synchronouswaitingroom.TcpSynchronousWaitingRoom;
import wei.yigulu.netty.AbstractTcpMasterBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;


/**
 * modbus的主站  向子站发送总召唤 获取子站的数据
 * <p>
 * 简单的主站  相对于主备机主站    仅有主机 不支持切换
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ModbusTcpMasterBuilder extends AbstractTcpMasterBuilder implements ModbusMasterBuilderInterface {


	protected SynchronousWaitingRoom synchronousWaitingRoom;

	/**
	 * 构造方法
	 *
	 * @param ip   ip
	 * @param port port
	 */
	public ModbusTcpMasterBuilder(String ip, Integer port) {
		super(ip, port);
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		if (this.channelInitializer == null) {
			this.channelInitializer = getDefaultChannelInitializer(this);
		}
		return this.channelInitializer;
	}


	@Override
	public SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() {
		if (this.synchronousWaitingRoom == null) {
			this.synchronousWaitingRoom = new TcpSynchronousWaitingRoom();
		}
		return this.synchronousWaitingRoom;
	}


	public static ProtocolChannelInitializer getDefaultChannelInitializer(AbstractTcpMasterBuilder masterBuilder) {
		return new ProtocolChannelInitializer<SocketChannel>(masterBuilder) {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ModbusTcpDelimiterHandler().setLog(masterBuilder.getLog()));
				ch.pipeline().addLast(new ModbusTcpMasterHandler((AbstractTcpMasterBuilder) builder));
			}
		};
	}
}
