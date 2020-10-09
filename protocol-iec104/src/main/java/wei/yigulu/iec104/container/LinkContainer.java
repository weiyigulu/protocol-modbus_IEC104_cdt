package wei.yigulu.iec104.container;

import io.netty.channel.ChannelId;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 储存着104的所有连接
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
public class LinkContainer {

	private static class LazyHolder {
		private static final LinkContainer INSTANCE = new LinkContainer();
	}

	/**
	 * Gets instance *
	 *
	 * @return the instance
	 */
	public static final LinkContainer getInstance() {
		return LinkContainer.LazyHolder.INSTANCE;
	}

	private LinkContainer() {
	}

	private Map<ChannelId, Iec104Link> links = new ConcurrentHashMap<>();


	/**
	 * Get link iec 104 link
	 *
	 * @param channelId channel id
	 * @return the iec 104 link
	 */
	public Iec104Link getLink(ChannelId channelId) {
		return this.links.get(channelId);
	}

	/**
	 * Get link iec 104 link
	 *
	 * @param ip   ip
	 * @param port port
	 * @return the iec 104 link
	 */
	public Iec104Link getLink(String ip, int port) {
		for (Map.Entry<ChannelId, Iec104Link> e : this.links.entrySet()) {
			if (e.getValue().getOppositeIp().equals(ip) && e.getValue().getOppositePort() == port) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * Get slave link iec 104 link
	 *
	 * @param ip   ip
	 * @param port port
	 * @return the iec 104 link
	 */
	public Iec104Link getSlaveLink(String ip, int port) {
		for (Map.Entry<ChannelId, Iec104Link> e : this.links.entrySet()) {
			if (e.getValue().getOppositeIp().equals(ip) && e.getValue().getOppositePort() == port && e.getValue().getOppositeRole() == Iec104Link.Role.SLAVER) {
				return e.getValue();
			}
		}
		return null;
	}


	/**
	 * Get master links list
	 *
	 * @param ip ip
	 * @return the list
	 */
	public List<Iec104Link> getMasterLinks(String ip) {
		List<Iec104Link> links = new ArrayList<>();
		for (Map.Entry<ChannelId, Iec104Link> e : this.links.entrySet()) {
			if (e.getValue().getOppositeIp().equals(ip) && e.getValue().getOppositeRole() == Iec104Link.Role.MASTER) {
				links.add(e.getValue());
			}
		}
		return links;
	}

}
