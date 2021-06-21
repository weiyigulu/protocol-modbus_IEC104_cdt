package wei.yigulu.modbus.netty;


import io.netty.channel.Channel;
import lombok.Getter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.ModbusSlaveDataContainer;
import wei.yigulu.modbus.domain.command.AbstractModbusCommand;
import wei.yigulu.netty.AbstractTcpSlaverBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;


/**
 * 104的子站  是向主站提供数据的 主站发送总召唤 子站响应主站的召唤
 * 向主站上送数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

@Accessors(chain = true)
public class ModbusTcpSlaverBuilder extends AbstractTcpSlaverBuilder implements ModbusSlaverInterface {


	@Getter
	private ModbusSlaveDataContainer modbusSlaveDataContainer = new ModbusSlaveDataContainer().setLog(getLog());

	public ModbusTcpSlaverBuilder(int port) {
		super(port);
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		return new ProtocolChannelInitializer(this) {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ch.pipeline().addLast(new ModbusTcpDelimiterHandler().setLog(getLog()));
				ch.pipeline().addLast(new ModbusTcpSlaverHandle((ModbusTcpSlaverBuilder) builder));
			}
		};
	}

	@Override
	public boolean receiveCommand(AbstractModbusCommand command) {
		return true;
	}
}
