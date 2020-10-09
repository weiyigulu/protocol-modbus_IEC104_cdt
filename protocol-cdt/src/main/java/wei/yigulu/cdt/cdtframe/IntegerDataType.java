package wei.yigulu.cdt.cdtframe;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wei.yigulu.utils.CrcUtils;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 遥测数据
 *
 * @author 修唯xiuwei
 **/
@NoArgsConstructor
public class IntegerDataType extends BaseDateType<Integer> {

	/**
	 * 质量位描述的map
	 */
	@Getter
	private Map<Integer, QualityDescription> qualityDescriptionMap;


	/**
	 * 整数数据类型
	 * 构造方法  map中的数据必须是2个以内 且必须是连续的  不足2个   其他位皆视为 false
	 * 如果数据不连续将可能在上送过程中改变被越过数据的原有值  可以不足2位 因为数据总量可能不足2的整数倍
	 * 由于cdt协议中一个功能码中有2个整数值  所有 最小的点位须为2的整数倍
	 *
	 * @param dates     数据
	 * @param qualities 品质
	 */

	public IntegerDataType(Map<Integer, Integer> dates, Map<Integer, QualityDescription> qualities) {
		if (dates.size() == 0) {
			throw new RuntimeException("数据个数不能为0");
		}
		if (dates.size() > 2) {
			throw new RuntimeException("数据个数超过二个");
		}
		Set<Integer> set = dates.keySet();
		if (Collections.min(set) % 2 != 0) {
			throw new RuntimeException("数据点位不是以2的整数倍开头");
		}
		if ((Collections.max(set) - Collections.min(set)) != dates.size() - 1) {
			throw new RuntimeException("数据点位不连续");
		}

		this.dates = dates;
		this.qualityDescriptionMap = qualities == null ? new HashMap<>() : qualities;
	}


	@Override
	public void readDates(byte[] bs) {
		//功能码处于00H-7FH之间的是遥测  86H-89H是总加遥测 忽略总加遥测
		//遥测二进制  b14位为1代表溢出,b15位为1 代表无效  有效数据位 为 b0-b10  b11为1时代表负数  以2的补码表述
		if (getFunctionNum() <= 0x7f) {
			this.dates = new HashMap<>(2);
			this.qualityDescriptionMap = new HashMap<>(2);
			this.dates.put(super.getFunctionNum() * 2, decode2Int(bs[0], bs[1]));
			this.qualityDescriptionMap.put(super.getFunctionNum() * 2, new QualityDescription(bs[1]));
			this.dates.put(super.getFunctionNum() * 2 + 1, decode2Int(bs[2], bs[3]));
			this.qualityDescriptionMap.put(super.getFunctionNum() * 2 + 1, new QualityDescription(bs[3]));
		}
	}

	/**
	 * 转化成CDT的int型
	 *
	 * @param b1
	 * @param b2
	 * @return
	 */
	private Integer decode2Int(Byte b1, Byte b2) {
		int i = (b1 & 0xff) | (b2 & 0x07) << 8;
		if ((b2 >> 3 & 0x01) == 1) {
			i = (2048 - i) * -1;
		}
		return i;
	}


	@Override
	public Map<Integer, Integer> getDates() {
		return this.dates;
	}

	@Override
	protected void encode(ByteBuffer byteBuffer) {
		int min = Collections.min(getDates().keySet());
		int val;
		int iVal;
		this.functionNum = min / 2;
		byte[] bytes = new byte[]{(byte) this.functionNum, 0, 0, 0, 0};
		for (int i = 0; i < 2; i++) {
			if (getDates().containsKey(min + i)) {
				val = getDates().get(min + i);
				if (val >= 00) {
					//正数
					bytes[2 * i + 1] = (byte) val;
					bytes[2 * i + 2] = (byte) (((byte) (val >> 8)) & 07);
				} else {
					//负数
					//清零前五位的数值
					iVal = (val * -1) & 0x07ff;
					//先减一再反转
					iVal = 2048 - iVal;
					bytes[2 * i + 1] = (byte) iVal;
					bytes[2 * i + 2] = (byte) (iVal >> 8);
					bytes[2 * i + 2] = (byte) (bytes[2 * i + 2] | (1 << 3));
				}
				if (val > 2047 || val < -2048) {
					//溢出
					bytes[2 * i + 2] = (byte) (bytes[2 * i + 2] | (1 << 6));
				}
				if (getQualityDescriptionMap().containsKey(min + i) &&
						!getQualityDescriptionMap().get(min + i).getInvalid()) {
					//无效
					bytes[2 * i + 2] = (byte) (bytes[2 * i + 2] | (1 << 7));
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
				s += "遥测点位：" + i + ";值：" + this.getDates().get(i) + " " + getQualityDescriptionMap().get(i) + "\n";
			}
		}
		return s;
	}


	@Data
	class QualityDescription {
		/**
		 * 是否溢出 false 即为不溢出
		 */
		Boolean overflow = false;
		/**
		 * 是否无效 false 即为有效
		 */
		Boolean invalid = false;

		public QualityDescription(Byte b) {
			if ((b >> 6 & 0x01) == 1) {
				this.overflow = true;
			}
			if ((b >> 7 & 0x01) == 1) {
				this.invalid = true;
			}
		}

		@Override
		public String toString() {
			return (overflow ? "溢出" : "") + " " + (invalid ? "无效" : "有效");
		}

	}
}
