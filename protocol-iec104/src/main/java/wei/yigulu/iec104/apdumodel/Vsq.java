package wei.yigulu.iec104.apdumodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Vsq
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
/**
 * vsq 可变限定词  分为 sq 和 num
 * 可变结构限定词  ASDU第一位
 * 该值为二位16进制数  先转成8位二进制
 * 二进制第8位 为0 单一信息元素寻址
 * 二进制第8位 为1 连续信息元素寻址
 * 剩下7位转为10进制 数值为信息元素数目
 * @author 修唯xiuwei
 * @version 3.0
 */
public class Vsq {

	/**
	 * Read byte vsq
	 *
	 * @param value value
	 * @return the vsq
	 */
	public Vsq readByte(Byte value) {
		original = value;
		String vsqFormat = String.format("%08d", Integer.parseInt(Integer.toBinaryString(this.original & 0xff)));
		//可变结构限定词，转为二进制后获取第8位
		sq = Integer.parseInt(vsqFormat.substring(0, 1));
		num = Integer.parseInt(vsqFormat.substring(1, 8), 2);
		return this;
	}

	/**
	 * vsq 的具体值
	 */
	byte original;

	/**
	 * 标制  是 顺序元素 还是 单一元素   第8位
	 */
	int sq;

	/**
	 * 信息体元素地址数量   0-7位
	 */
	int num;

	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		if (this.getSq() == 1) {
			buffer.add((byte) (this.getNum() | 0x80));
		} else {
			buffer.add((byte) this.getNum());
		}
	}
}
