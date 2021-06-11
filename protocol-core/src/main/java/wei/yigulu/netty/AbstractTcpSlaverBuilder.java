package wei.yigulu.netty;


import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * slave是向主站提供数据的 主站发送总召唤 子站响应主站的召唤
 * 向主站上送数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public abstract class AbstractTcpSlaverBuilder extends AbstractTcpServerBuilder implements SlaverInterface {


	public AbstractTcpSlaverBuilder(int port) {
		super(port);
	}
}
