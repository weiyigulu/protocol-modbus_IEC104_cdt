package wei.yigulu.iec104.apdumodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Cot
 */
@AllArgsConstructor
@Data
@NoArgsConstructor

/**
 * 传输原因
 * @author 修唯xiuwei
 * @version 3.0
 */
public class Cot {

	/**
	 * Read byte cot
	 *
	 * @param value value
	 * @return the cot
	 */
	public Cot readByte(Byte value) {
		original = value;
		String cotFormat = value > 0 ? String.format("%08d", Integer.parseInt(Integer.toBinaryString(original))) : "00000011";
		//可变结构限定词，转为二进制后获取第8位
		if (Integer.parseInt(cotFormat.substring(0, 1)) == 1) {
			test = true;
		}
		if (Integer.parseInt(cotFormat.substring(1, 2)) == 1) {
			negativeConfirm = true;
		}
		not = Integer.parseInt(cotFormat.substring(2, 8), 2);
		return this;
	}

	/**
	 * cot 的具体值
	 */
	byte original;
	/**
	 * 测试状态   第八位
	 * 0---false---未试验
	 * 1---true----已试验
	 */
	private boolean test;

	/**
	 * 认证方式   第七位
	 * 0---false----肯定认可
	 * 1---true-----否定认可
	 */
	boolean negativeConfirm;

	/**
	 * 传输原因序号   1-6位
	 * 3：突发，自发
	 * 4：初始化
	 * 6：激活
	 * 7：激活确认
	 * 8：停止激活
	 * 9：停止激活确认
	 * 10：激活终止
	 * 20：响应站召唤
	 */
	int not;

	/**
	 * Encode *
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		if (this.isTest()) {
			if (this.isNegativeConfirm()) {
				buffer.add((byte) (this.getNot() | 0xC0));
			} else {
				buffer.add((byte) (this.getNot() | 0x80));
			}
		} else {
			if (this.isNegativeConfirm()) {
				buffer.add((byte) (this.getNot() | 0x40));
			} else {
				buffer.add((byte) this.getNot());
			}
		}
	}
}
