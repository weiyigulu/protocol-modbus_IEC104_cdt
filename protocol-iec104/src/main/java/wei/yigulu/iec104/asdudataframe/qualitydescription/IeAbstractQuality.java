package wei.yigulu.iec104.asdudataframe.qualitydescription;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.exception.Iec104Exception;


/**
 * 品质描述 的抽象类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IeAbstractQuality {

	public static final int OCCUPYBYTES = 1;

	/**
	 * 品质的值
	 */
	protected int value;

	/**
	 * 封锁标志 0-未被封锁；1被封锁·
	 */
	protected boolean blocked;

	/**
	 * 取代标识  0-未被取代；1被取代
	 */
	protected boolean substituted;

	/**
	 * 当前值标志 0当前值；1非当前值
	 */
	protected boolean notTopical;

	/**
	 * 有效标志 0有效；1无效
	 */
	protected boolean invalid;


	/**
	 * Ie abstract quality
	 *
	 * @param is is
	 */
	public IeAbstractQuality(ByteBuf is) throws Iec104Exception {
		if (is.readableBytes() < OCCUPYBYTES) {
			throw new Iec104Exception(3301, "可用字节不足，不能进行读取");
		}
		this.value = (is.readByte() & 0xff);
		this.blocked = (value & 0x10) == 0x10;
		this.substituted = (value & 0x20) == 0x20;
		this.notTopical = (value & 0x40) == 0x40;
		this.invalid = (value & 0x80) == 0x80;
	}


	/**
	 * 描述品质位 为1位
	 *
	 * @return int
	 */
	public int encode() {
		int v = 0x00;
		if (blocked) {
			v |= 0x10;
		}
		if (substituted) {
			v |= 0x20;
		}
		if (notTopical) {
			v |= 0x40;
		}
		if (invalid) {
			v |= 0x80;
		}
		return v;
	}


	@Override
	public String toString() {
		return "被封锁: " + isBlocked() + ", 被取代: " + isSubstituted() + ", 非当前值: " + isNotTopical()
				+ ", 无效: " + isInvalid();
	}
}
