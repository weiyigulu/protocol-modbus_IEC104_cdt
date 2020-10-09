package wei.yigulu.netty;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * 负责监听启动时连接失败，重新连接功能
 * 带有主备切换功能的 连接监听
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@AllArgsConstructor
@NoArgsConstructor
public class HSConnectionListener implements ChannelFutureListener {

	private Logger log;

	private AbstractHSTcpMasterBuilder masterBuilder;

	private int retryTimes;

	/**
	 * Hs connection listener
	 *
	 * @param masterBuilder master builder
	 */
	public HSConnectionListener(AbstractHSTcpMasterBuilder masterBuilder) {
		this.masterBuilder = masterBuilder;
		this.log = masterBuilder.getLog();
	}

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (channelFuture == null || channelFuture.channel() == null || !channelFuture.channel().isActive()) {
			this.masterBuilder.getOrCreateWorkGroup().schedule(() -> {
				try {
					if (this.retryTimes < 10) {
						log.error("服务端{}:{}链接不上，开始重连操作,第{}次尝试", this.masterBuilder.getIp(), this.masterBuilder.getPort(), retryTimes);
						masterBuilder.create();
						log.warn("重试连接失败");
						this.retryTimes++;
					} else {
						if (!StringUtil.isNullOrEmpty(this.masterBuilder.getSpareIp()) || (this.masterBuilder.getSparePort() != null && this.masterBuilder.getSparePort() != 0)) {
							log.info("服务端{}:{}链接不上，切换主备机{}:{}", this.masterBuilder.getIp(), this.masterBuilder.getPort(), this.masterBuilder.getSpareIp(), this.masterBuilder.getSparePort());
							this.masterBuilder.switchover();
						}
						this.masterBuilder.refreshLoopGroup();
						this.retryTimes = 0;
						masterBuilder.create();
						log.info("重置重试次数=0");
					}
				} catch (Exception e) {
					log.error("ModbusMaster重试连接时发生异常", e);
					this.masterBuilder.refreshLoopGroup();
					this.retryTimes = 0;
					try {
						operationComplete(channelFuture);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}, 3L, TimeUnit.SECONDS);
		} else {
			log.info("服务端{}:{}链接成功...", this.masterBuilder.getIp(), this.masterBuilder.getPort());
		}
	}
}

