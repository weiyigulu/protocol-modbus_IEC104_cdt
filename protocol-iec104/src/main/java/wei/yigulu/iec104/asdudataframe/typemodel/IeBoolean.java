package wei.yigulu.iec104.asdudataframe.typemodel;


import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.asdudataframe.qualitydescription.IeAbstractQuality;


/**
 * 遥信值
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IeBoolean extends IeAbstractQuality {

	/**
	 * 信息状态，是否开闸 1：合；2：开
	 */
	protected boolean on;

	/**
	 * Ie boolean
	 *
	 * @param is is
	 */
	public IeBoolean(ByteBuf is) {
		super(is);
		this.on = (value & 0x01) == 0x01;
	}

	@Override
	public int encode() {
		int v = super.encode();
		if (on) {
			v |= 0x01;
		}
		return v;
	}


	@Override
	public String toString() {
		return "开关量, 是否开闸: " + isOn() + ", " + super.toString() + ";\n";
	}
}
