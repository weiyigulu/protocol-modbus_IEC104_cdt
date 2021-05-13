package wei.yigulu.netty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 104主从站的基类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class BaseProtocolBuilder {

	public BaseProtocolBuilder() {
		//ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
	}

	@Setter
	@Getter
	@Accessors(chain = true)
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Builder id
	 */
	@Getter
	protected String builderId = UUID.randomUUID().toString();

	/**
	 * Config info map
	 */
	@Getter
	protected Map<String, Object> configInfoMap = new HashMap<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BaseProtocolBuilder that = (BaseProtocolBuilder) o;
		return builderId.equals(that.builderId);
	}

	@Override
	public int hashCode() {
		return builderId.hashCode();
	}
}
