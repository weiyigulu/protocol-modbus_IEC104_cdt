package wei.yigulu.modbus.netty;


import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.domain.synchronouswaitingroom.TcpSynchronousWaitingRoom;
import wei.yigulu.netty.AbstractHSTcpMasterBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;


/**
 * modbus的主站  向子站发送总召唤 获取子站的数据
 * <p>
 * 主备模式的 主站 可以切换主备机
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class HSModbusTcpMasterBuilder extends AbstractHSTcpMasterBuilder implements ModbusMasterBuilderInterface {

	protected SynchronousWaitingRoom synchronousWaitingRoom;

	public HSModbusTcpMasterBuilder(String ip, Integer port) {
		super(ip, port);
	}


	@Override
	public SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() {
		if (this.synchronousWaitingRoom == null) {
			this.synchronousWaitingRoom = new TcpSynchronousWaitingRoom();
		}
		return this.synchronousWaitingRoom;
	}

	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		if (this.channelInitializer == null) {
			this.channelInitializer = ModbusTcpMasterBuilder.getDefaultChannelInitializer(this);
		}
		return this.channelInitializer;
	}
}
