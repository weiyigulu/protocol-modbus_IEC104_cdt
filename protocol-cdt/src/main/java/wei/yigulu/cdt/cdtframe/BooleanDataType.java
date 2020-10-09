package wei.yigulu.cdt.cdtframe;

import lombok.NoArgsConstructor;
import wei.yigulu.utils.CrcUtils;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 遥信数据类型
 *
 * @author 修唯xiuwei
 **/
@NoArgsConstructor
public class BooleanDataType extends BaseDateType<Boolean> {

	/**
	 * CDT协议00-f0是遥测  f0之后是遥信
	 */
	private static final Integer YXSTART = 0xf0;

	/**
	 * 构造方法  map中的数据必须是32个以内 且必须是连续的  不足32个   其他位皆视为 false
	 * 如果数据不连续将可能在上送过程中改变被越过数据的原有值  可以不足32位 因为数据总量可能不足32的整数倍
	 * 由于cdt协议中一个功能码中有32个布尔值  所有 最小的点位须为32的整数倍
	 *
	 * @param dates 数据  点位---值
	 */
	public BooleanDataType(Map<Integer, Boolean> dates) {
		if (dates.size() == 0) {
			throw new RuntimeException("数据个数不能为0");
		}
		if (dates.size() > 32) {
			throw new RuntimeException("数据个数超过三十二个");
		}
		Set<Integer> set = dates.keySet();
		if (Collections.min(set) % 32 != 0) {
			throw new RuntimeException("数据点位不是以32的整数倍开头");
		}
		if ((Collections.max(set) - Collections.min(set)) != dates.size() - 1) {
			throw new RuntimeException("数据点位不连续");
		}
		this.dates = dates;

	}

	@Override
	public void readDates(byte[] bs) {
		this.dates = new HashMap<>(32);
		int num = (super.getFunctionNum() - YXSTART) * 32;
		for (int i = 0; i < bs.length; i++) {
			this.dates.put(num + i * 8, (bs[i] & 0x01) == 0x01);
			this.dates.put(num + i * 8 + 1, (bs[i] & 0x02) == 0x02);
			this.dates.put(num + i * 8 + 2, (bs[i] & 0x04) == 0x04);
			this.dates.put(num + i * 8 + 3, (bs[i] & 0x08) == 0x08);
			this.dates.put(num + i * 8 + 4, (bs[i] & 0x10) == 0x10);
			this.dates.put(num + i * 8 + 5, (bs[i] & 0x20) == 0x20);
			this.dates.put(num + i * 8 + 6, (bs[i] & 0x40) == 0x40);
			this.dates.put(num + i * 8 + 7, (bs[i] & 0x80) == 0x80);
		}
	}

	@Override
	public Map<Integer, Boolean> getDates() {
		return this.dates;
	}

	@Override
	protected void encode(ByteBuffer byteBuffer) {
		int min = Collections.min(getDates().keySet());
		this.functionNum = min / 32 + YXSTART;
		byte[] bytes = new byte[]{(byte) this.functionNum, 0, 0, 0, 0};
		int position;
		for (int i = 1; i < 5; i++) {
			for (int j = 0; j < 8; j++) {
				position = (i - 1) * 8 + j + min;
				if (getDates().containsKey(position)) {
					if (getDates().get(position)) {
						bytes[i] = (byte) (bytes[i] | (1 << j));
					}
				}
			}
		}
		byteBuffer.put(bytes);
		byteBuffer.put((byte) CrcUtils.generateCRC8(bytes));
	}


	@Override
	public String toString() {
		String s = "";
		if (this.dates != null) {
			for (Integer i : this.getDates().keySet()) {
				s += "遥信点位：" + i + ";值：" + this.getDates().get(i) + "\n";
			}
		}
		return s;
	}
}
