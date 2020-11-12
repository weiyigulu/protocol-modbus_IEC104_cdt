package wei.yigulu.modbus.netty;

import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.synchronouswaitingroom.RtuSynchronousWaitingRoom;
import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.netty.AbstractRtuModeBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;
import wei.yigulu.purejavacomm.PureJavaCommChannel;

/**
 * cdt的客户端
 *
 * @author: xiuwei
 * @version:
 */
@Accessors(chain = true)
@Slf4j
public class ModbusRtuMasterBuilder extends AbstractRtuModeBuilder implements ModbusMasterBuilderInterface {


	private SynchronousWaitingRoom synchronousWaitingRoom;

	public ModbusRtuMasterBuilder(String commPortId) {
		super(commPortId);
	}

	@Override
	public SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() throws ModbusException {
		if (this.synchronousWaitingRoom == null) {
			this.synchronousWaitingRoom = new RtuSynchronousWaitingRoom();
		}
		return this.synchronousWaitingRoom;
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		if (this.channelInitializer == null) {
			this.channelInitializer = new ProtocolChannelInitializer<PureJavaCommChannel>(this) {
				@Override
				protected void initChannel(PureJavaCommChannel ch) throws Exception {
					ch.pipeline().addLast(new ModbusRtuMasterDelimiterHandler().setLog(getLog()));
					ch.pipeline().addLast(new ModbusRtuMasterHandler((ModbusRtuMasterBuilder) builder));
				}
			};
		}
		return this.channelInitializer;
	}
}
