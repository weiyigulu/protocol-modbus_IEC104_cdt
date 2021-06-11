package wei.yigulu.netty;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * 负责监听启动时连接失败，重新连接功能
 * 相对与主备模式  该连接监听仅支持单主机模式
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class SimpleTcpConnectionListener implements ChannelFutureListener {

	private Logger log;

	private AbstractTcpMasterBuilder masterBuilder;

	ScheduledFuture<?> future;

	/**
	 * Only host connection listener
	 *
	 * @param masterBuilder master builder
	 */
	public SimpleTcpConnectionListener(AbstractTcpMasterBuilder masterBuilder) {
		this.masterBuilder = masterBuilder;
		this.log = masterBuilder.getLog();
	}


	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (channelFuture == null || channelFuture.channel() == null || !channelFuture.channel().isActive()) {
			this.future = this.masterBuilder.getOrCreateWorkGroup().schedule(() -> {
				try {
					if (masterBuilder.future == null || !masterBuilder.future.channel().isActive()) {
						log.error("服务端{}:{}链接不上，开始重连操作", this.masterBuilder.getIp(), this.masterBuilder.getPort());
						masterBuilder.create();
					} else {
						log.warn("masterBuilder在延迟过程中已由其他线程连接成功，此处略过重连");
					}
				} catch (Exception e) {
					log.error("TcpMaster重试连接时发生异常", e);
					try {
						operationComplete(channelFuture);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}, 6L, TimeUnit.SECONDS);
		} else {
			log.warn("masterBuilder已经连接成功，不进行重连操作");
		}
	}
}

