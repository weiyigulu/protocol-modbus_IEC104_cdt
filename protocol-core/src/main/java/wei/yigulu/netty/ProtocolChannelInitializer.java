package wei.yigulu.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * modbus 的master  在netty上的通道实现
 *
 * @author: xiuwei
 * @version:
 */
public abstract class ProtocolChannelInitializer<T extends Channel> extends ChannelInitializer<T> {

	protected BaseProtocolBuilder builder;

	public ProtocolChannelInitializer(BaseProtocolBuilder builder) {
		this.builder = builder;
	}


}
