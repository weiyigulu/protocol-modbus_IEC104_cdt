package wei.yigulu.modbus.netty;


import lombok.Getter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.ModbusSlaveDataContainer;
import wei.yigulu.modbus.domain.command.AbstractModbusCommand;
import wei.yigulu.netty.AbstractRtuModeBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;
import wei.yigulu.purejavacomm.PureJavaCommChannel;


/**
 * modbus的子站  是向主站提供数据的 主站发送总召唤 子站响应主站的召唤
 * 向主站上送数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */


@Accessors(chain = true)
public class ModbusRtuSlaverBuilder extends AbstractRtuModeBuilder implements ModbusSlaverInterface {

	@Getter
	private ModbusSlaveDataContainer modbusSlaveDataContainer = new ModbusSlaveDataContainer().setLog(getLog());


	public ModbusRtuSlaverBuilder(String commPortId) {
		super(commPortId);
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		if (this.channelInitializer == null) {
			this.channelInitializer = new ProtocolChannelInitializer<PureJavaCommChannel>(this) {
				@Override
				protected void initChannel(PureJavaCommChannel ch) throws Exception {
					ch.pipeline().addLast(new ModbusRtuSlaverDelimiterHandler().setLog(getLog()));
					ch.pipeline().addLast(new ModbusRtuSlaverHandler((ModbusRtuSlaverBuilder) builder));
				}
			};
		}
		return this.channelInitializer;
	}

	@Override
	public boolean receiveCommand(AbstractModbusCommand command) {
		return true;
	}
}
