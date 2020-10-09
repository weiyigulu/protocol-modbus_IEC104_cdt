package wei.yigulu.connectfilterofslave;

import io.netty.channel.Channel;

/**
 * 连接的适配器
 *
 * @author: xiuwei
 * @version:
 */
public interface ConnectFilter {

	/**
	 * 过滤器
	 * 对接入的设备进行过滤
	 *
	 * @param channel 通道
	 * @return 1：允许通过  0：交给下一个过滤器判断  -1：不允许通过
	 */
	int filter(Channel channel);

}
