import wei.yigulu.cdt.cdtframe.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cdt数据处理类的基础类
 *
 * @author: xiuwei
 * @version:
 */
public class LocalCDTDataTransmitter extends AbstractCDTDataTransmitter {


	@Override
	public List<CDTFrameBean> transmitImportantYc() {
		return null;
	}

	@Override
	public List<CDTFrameBean> transmitSecondYc() {
		List<CDTFrameBean> list = new ArrayList<>();
		Map<Integer, Integer> map = new HashMap<>();
		map.put(0, -1101);
		map.put(1, 22);
		List<BaseDateType> list1 = new ArrayList<>();
		IntegerDataType integerDataType = new IntegerDataType(map, null);
		list1.add(integerDataType);
		CDTFrameBean cdtFrameBean = new CDTFrameBean(list1);
		list.add(cdtFrameBean);
		return list;
	}

	@Override
	public List<CDTFrameBean> transmitCommonYc() {
		return null;
	}

	@Override
	public List<CDTFrameBean> transmitYx() {
		List<CDTFrameBean> list = new ArrayList<>();
		Map<Integer, Boolean> map = new HashMap<>();
		map.put(0, false);
		map.put(1, true);
		map.put(2, true);
		map.put(3, true);
		map.put(4, false);
		map.put(5, false);
		map.put(6, false);
		map.put(7, true);
		List<BaseDateType> list1 = new ArrayList<>();
		BooleanDataType dataType = new BooleanDataType(map);
		list1.add(dataType);
		CDTFrameBean cdtFrameBean = new CDTFrameBean(list1);
		list.add(cdtFrameBean);
		return list;
	}
}
