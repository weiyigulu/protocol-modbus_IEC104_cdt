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
public abstract class AbstractCDTDataHandler {


	@Setter
	@Getter
	@Accessors(chain = true)
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	public void processFrame(CDTFrameBean frameBean) {
		List<BaseDateType> dates = frameBean.getDates();
		switch (frameBean.getCdtType()) {
			//重要遥测
			case IMPORTANTYC:
				processImportantYc(dates);
				break;
			//次要遥测
			case SECONDYC:
				processSecondYc(dates);
				break;
			//一般遥测
			case COMMONYC:
				processCommonYc(dates);
				break;
			// 遥信
			case YX:
				processYx(dates);
				break;
			default:
		}
	}

	/**
	 * 处理重要遥测数据
	 *
	 * @param dates 数据帧集合
	 */
	protected abstract void processImportantYc(List<BaseDateType> dates);

	/**
	 * 处理次要遥测数据
	 *
	 * @param dates 数据帧集合
	 */
	protected abstract void processSecondYc(List<BaseDateType> dates);

	/**
	 * 处理一般遥测数据
	 *
	 * @param dates 数据帧集合
	 */
	protected abstract void processCommonYc(List<BaseDateType> dates);

	/**
	 * 处理遥信数据
	 *
	 * @param dates 数据帧集合
	 */
	protected abstract void processYx(List<BaseDateType> dates);


	public void connected() {

	}


	public void disconnected() {

	}


}
