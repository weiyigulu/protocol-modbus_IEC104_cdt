package wei.yigulu.cdt.cdtframe;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * CDT 数据的处理类
 *
 * @author: xiuwei
 * @version:
 */
public abstract class AbstractCDTDataTransmitter {

	@Setter
	@Getter
	@Accessors(chain = true)
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 发送重要遥测
	 *
	 * @return 数据帧list
	 */
	public abstract List<CDTFrameBean> transmitImportantYc();

	/**
	 * 发送次要遥测
	 *
	 * @return 数据帧list
	 */
	public abstract List<CDTFrameBean> transmitSecondYc();

	/**
	 * 发送一般遥测
	 *
	 * @return 数据帧list
	 */
	public abstract List<CDTFrameBean> transmitCommonYc();

	/**
	 * 发送遥信
	 *
	 * @return 数据帧list
	 */
	public abstract List<CDTFrameBean> transmitYx();


	public void connected() {

	}


	public void disconnected() {

	}


}
