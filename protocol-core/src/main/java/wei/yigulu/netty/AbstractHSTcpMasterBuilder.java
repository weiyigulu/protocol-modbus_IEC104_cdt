package wei.yigulu.netty;


import io.netty.channel.ChannelFutureListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * 104的主站  向子站发送总召唤 获取子站的数据
 * <p>
 * 主备模式的 主站 可以切换主备机
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public abstract class AbstractHSTcpMasterBuilder extends AbstractTcpMasterBuilder {


	/**
	 * Hs master builder
	 *
	 * @param ip   ip
	 * @param port port
	 */
	public AbstractHSTcpMasterBuilder(String ip, Integer port) {
		super(ip, port);
	}

	/**
	 * Hs master builder
	 *
	 * @param ip        ip
	 * @param port      port
	 * @param spareIp   备 ip
	 * @param sparePort 备 port
	 */
	public AbstractHSTcpMasterBuilder(String ip, Integer port, String spareIp, Integer sparePort) {
		super(ip, port);
		this.spareIp = spareIp;
		this.sparePort = sparePort;
	}

	/**
	 * 备对端ip
	 */
	@Getter
	@Setter
	private String spareIp;

	/**
	 * 备对端port
	 */
	@Getter
	@Setter
	private Integer sparePort;


	@Override
	public ChannelFutureListener getOrCreateConnectionListener() {
		if (this.connectionListener == null) {
			this.connectionListener = new HSConnectionListener(this);
		}
		return this.connectionListener;
	}

	/**
	 * 切换主备机
	 */
	public void switchover() {
		String temporaryIp;
		int temporaryPort;
		if (spareIp != null && !"".equals(spareIp)) {
			temporaryIp = this.ip;
			this.ip = this.spareIp;
			this.spareIp = temporaryIp;
		}
		if (sparePort != 0) {
			temporaryPort = this.port;
			this.port = this.sparePort;
			this.sparePort = temporaryPort;
		}
	}


}
