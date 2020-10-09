import wei.yigulu.cdt.cdtframe.AbstractCDTDataHandler;
import wei.yigulu.cdt.cdtframe.BaseDateType;

import java.util.List;

/**
 * cdt数据处理类的基础类
 *
 * @author: xiuwei
 * @version:
 */
public class LocalCDTDataHandler extends AbstractCDTDataHandler {
	DataContainer dataContainer = DataContainer.getInstance();


	@Override
	protected void processImportantYc(List<BaseDateType> dates) {
		saveYc(dates);
	}

	@Override
	protected void processSecondYc(List<BaseDateType> dates) {
		saveYc(dates);
	}

	@Override
	protected void processCommonYc(List<BaseDateType> dates) {
		saveYc(dates);
	}

	@Override
	protected void processYx(List<BaseDateType> dates) {
		for (BaseDateType dateType : dates) {
			dateType.getDates().forEach((k, v) -> dataContainer.putYx((Integer) k, (Boolean) v));
		}
	}


	private void saveYc(List<BaseDateType> dates) {
		for (BaseDateType dateType : dates) {
			dateType.getDates().forEach((k, v) -> dataContainer.putYc((Integer) k, (Integer) v));
		}

	}
}
