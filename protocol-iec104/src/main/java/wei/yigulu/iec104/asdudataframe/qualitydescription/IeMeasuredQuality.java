package wei.yigulu.iec104.asdudataframe.qualitydescription;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.exception.Iec104Exception;

/**
 * 测量值品质描述
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class IeMeasuredQuality extends IeAbstractQuality {

	public static final int OCCUPYBYTES = 1;

	/**
	 * 是否溢出 0 代表未溢出;1 代表溢出
	 */
	protected boolean overflow;

	/**
	 * Ie measured quality
	 *
	 * @param is is
	 */
	public IeMeasuredQuality(ByteBuf is) throws Iec104Exception {
		super(is);
		this.overflow = (value & 0x01) == 0x01;
	}

	@Override
	public int encode() {
		int v = super.encode();
		if (overflow) {
			v |= 0x01;
		}
		return v;
	}


	@Override
	public boolean equals(Object o) {
		return false;
	}


	@Override
	public String toString() {
		return "品质描述 : { 溢出: " + overflow + ", " + super.toString() + "}";
	}
}
