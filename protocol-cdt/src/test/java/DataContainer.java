import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据容器
 *
 * @author 修唯xiuwei
 **/
public class DataContainer {

	private static class LazyHolder {
		private static final DataContainer INSTANCE = new DataContainer();
	}

	private DataContainer() {
	}

	public static final DataContainer getInstance() {
		return DataContainer.LazyHolder.INSTANCE;
	}

	private volatile Map<Integer, Float> ycData = new ConcurrentHashMap<>();

	private volatile Map<Integer, Boolean> yxData = new ConcurrentHashMap<>();

	public void putYc(int address, float f) {
		this.ycData.put(address, f);
	}

	public void putYx(int address, boolean b) {
		this.yxData.put(address, b);
	}

	public Map<Integer, Float> getYc() {
		return this.ycData;
	}

	public Map<Integer, Boolean> getYx() {
		return this.yxData;
	}

	@Override
	public String toString() {
		return (JSON.toJSONString(this.yxData) + JSON.toJSONString(this.ycData));
	}


}
