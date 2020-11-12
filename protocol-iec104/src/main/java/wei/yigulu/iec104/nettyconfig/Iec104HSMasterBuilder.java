package wei.yigulu.iec104.nettyconfig;


import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import wei.yigulu.netty.AbstractHSTcpMasterBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;


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
public class Iec104HSMasterBuilder extends AbstractHSTcpMasterBuilder {


	/**
	 * Hs master builder
	 *
	 * @param ip   ip
	 * @param port port
	 */
	public Iec104HSMasterBuilder(String ip, Integer port) {
		super(ip, port);
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		return Iec104MasterBuilder.getDefaultChannelInitializer(this);
	}


}
