package wei.yigulu.connectfilterofslave;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器管理器
 *
 * @author: xiuwei
 * @version:
 */
public class ConnectFilterManager {

	private List<ConnectFilter> filters = new ArrayList<>();

	public void appendFilter(ConnectFilter connectFilter) {
		this.filters.add(connectFilter);
	}

	public void removeFilter(ConnectFilter connectFilter) {
		this.filters.remove(connectFilter);
	}

	/**
	 * 判断该连接是否通过判断链
	 *
	 * @return true  允许通过  false  不允许通过
	 */
	public boolean verdict(Channel channel) {
		if (this.filters.size() == 0) {
			return true;
		}
		int i;
		for (ConnectFilter filter : this.filters) {
			try {
				i = filter.filter(channel);
			} catch (Exception e) {
				i = 0;
			}
			if (i == 1) {
				return true;
			} else if (i == -1) {
				return false;
			} else if (i == 0) {
				continue;
			}
		}
		return true;
	}


}
