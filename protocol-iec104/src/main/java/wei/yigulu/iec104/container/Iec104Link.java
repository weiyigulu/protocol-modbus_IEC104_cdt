package wei.yigulu.iec104.container;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 在netty 中的104连接
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@NoArgsConstructor
public class Iec104Link {

	/**
	 * Iec 104 link
	 *
	 * @param channel      channel
	 * @param ip           ip
	 * @param port         port
	 * @param oppositeRole opposite role
	 */
	public Iec104Link(Channel channel, String ip, Integer port, Role oppositeRole, Logger logger) {
		this.channel = channel;
		this.oppositeIp = ip;
		this.oppositePort = port;
		this.oppositeRole = oppositeRole;
		this.log = logger;
		iReceive = 0;
		iSend = 0;
	}

	private String oppositeIp;

	private Integer oppositePort;

	private Channel channel;

	/**
	 * Role
	 */
	public enum Role {
		/**
		 * 通道对端在该通道扮演的104角色
		 */
		SLAVER,
		/**
		 * Master role
		 */
		MASTER
	}

	/**
	 * Opposite role
	 */
	public Role oppositeRole;

	private int iReceive;

	private int iSend;

	private LinkState linkState = LinkState.NORMAL;

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Link state
	 */
	public enum LinkState {
		/**
		 * 通道正常
		 */
		NORMAL,
		/**
		 * 通道对方失去响应
		 */
		LOSSRES,
		/**
		 * 通道对方断开连接
		 */
		DISCONN,
		/**
		 * 我方丢失 通道对方放出的 i帧
		 */
		LOSEREC,
		/**
		 * 通道对方丢失 我方发出的i帧
		 */
		LOSESEND
	}


}
