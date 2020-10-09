package wei.yigulu.netty;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 负责监听启动时连接失败，重新连接功能
 *
 * @author 修唯xiuwei
 * @create 2019-03-13 14:15
 * @Email 524710549@qq.com
 **/
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class RtuModeConnectionListener implements ChannelFutureListener {

	private AbstractRtuModeBuilder abstractRtuClient;

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (!channelFuture.channel().isActive()) {
			final EventLoop loop = channelFuture.channel().eventLoop();
			loop.schedule(() -> {
				abstractRtuClient.getLog().error("RTU:{}端链接不上，开始重连操作...", abstractRtuClient.getCommPortId());
				channelFuture.channel().closeFuture();
				abstractRtuClient.create();
			}, 4L, TimeUnit.SECONDS);
		} else {
			abstractRtuClient.getLog().info("RTU:{}端链接成功...", abstractRtuClient.getCommPortId());
		}
	}
}

