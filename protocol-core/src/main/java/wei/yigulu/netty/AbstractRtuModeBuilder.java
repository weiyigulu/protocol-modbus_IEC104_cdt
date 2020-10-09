package wei.yigulu.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.purejavacomm.PureJavaCommChannel;
import wei.yigulu.purejavacomm.PureJavaCommChannelConfig;
import wei.yigulu.purejavacomm.PureJavaCommChannelOption;
import wei.yigulu.purejavacomm.PureJavaCommDeviceAddress;

/**
 * 使用rtu的客户端
 *
 * @author: xiuwei
 * @version:
 */
@Accessors(chain = true)
@Slf4j
public abstract class AbstractRtuModeBuilder extends AbstractMasterBuilder {


	/**
	 * com口名称
	 */
	@Getter
	@Setter
	private String commPortId;
	/**
	 * 波特率
	 */
	@Getter
	@Setter
	private int baudRate = 9600;
	/**
	 * 数据位
	 */
	@Getter
	@Setter
	private PureJavaCommChannelConfig.Databits dataBits = PureJavaCommChannelConfig.Databits.DATABITS_8;
	/**
	 * 停止位
	 */
	@Getter
	@Setter
	private PureJavaCommChannelConfig.Stopbits stopBits = PureJavaCommChannelConfig.Stopbits.STOPBITS_1;
	/**
	 * 校验位
	 */
	@Getter
	@Setter
	private PureJavaCommChannelConfig.Paritybit parity = PureJavaCommChannelConfig.Paritybit.NONE;


	public AbstractRtuModeBuilder(String commPortId) {
		this.commPortId = commPortId;
	}


	@Override
	public void create() {
		try {
			this.future = getOrCreateBootstrap().connect(new PureJavaCommDeviceAddress(this.commPortId));
			future.addListener(getOrCreateConnectionListener());
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public EventLoopGroup getOrCreateWorkGroup() {
		if (this.workGroup == null) {
			this.workGroup = new OioEventLoopGroup();
		}
		return this.workGroup;
	}

	@Override
	public Bootstrap getOrCreateBootstrap() {
		if (this.bootstrap == null) {
			this.bootstrap = new Bootstrap();
			bootstrap.group(getOrCreateWorkGroup());
			bootstrap.channel(PureJavaCommChannel.class);
			bootstrap.handler(getOrCreateChannelInitializer());
			bootstrap.option(PureJavaCommChannelOption.BAUD_RATE, baudRate);
			bootstrap.option(PureJavaCommChannelOption.DATA_BITS, dataBits);
			bootstrap.option(PureJavaCommChannelOption.STOP_BITS, stopBits);
			bootstrap.option(PureJavaCommChannelOption.PARITY_BIT, parity);
		}
		return this.bootstrap;
	}

	@Override
	public ChannelFutureListener getOrCreateConnectionListener() {
		if (this.connectionListener == null) {
			this.connectionListener = new RtuModeConnectionListener(this);
		}
		return this.connectionListener;
	}

	@Override
	protected abstract ProtocolChannelInitializer getOrCreateChannelInitializer();
}
