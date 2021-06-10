package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 布尔的命令值，针对双点遥信
 *
 * @author: xiuwei
 * @version:
 */
public class IeDoubleBooleanCommand implements IecDataInterface {

	DoubleBoolean val = DoubleBoolean.OFF;
	/**
	 * 命令类型
	 */
	CommandType commandType = CommandType.CHOICE;
	/**
	 * 控制属性
	 */
	ControlProperty controlProperty = ControlProperty.UNDEFINED;


	public IeDoubleBooleanCommand(Integer val) {
		this.val = DoubleBoolean.valueOf(val);
	}

	public IeDoubleBooleanCommand(ByteBuf is) {
		int b = is.readUnsignedByte();
		this.val =  DoubleBoolean.valueOf(b & 3);
		this.commandType = CommandType.valueOf(b >> 7 & 1);
		this.controlProperty = ControlProperty.valueOf((b & 127) >> 2);
	}

	/**
	 * Encode  编码
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		int b = 0;
		b = b | this.val.code;
		b = b | (this.controlProperty.code << 2);
		b = b | (this.commandType.code << 7);
		buffer.add((byte) b);
	}

	@Override
	public DoubleBoolean getIecValue() {
		return val;
	}

	@Override
	public String toString() {
		String s;
		s = "值：" + val.msg + ";";
		s += "命令类型：" + commandType.msg + ";";
		s += "控制属性：" + controlProperty.msg + ";";
		return s;
	}

	@AllArgsConstructor
	public enum DoubleBoolean {
		/**
		 * 不确定
		 */
		INDETERMINACY(0, "不确定"),
		/**
		 * 开启 闭合
		 */
		ON(2, "合"),
		/**
		 * 关闭 断开
		 */
		OFF(1, "分"),
		/**
		 * 不确定
		 */
		INDETERMINACY2(3, "不确定");

		private Integer code;

		private String msg;

		public static DoubleBoolean valueOf(Integer code) {
			if (code == 0) {
				return DoubleBoolean.INDETERMINACY;
			} else if (code == 1) {
				return DoubleBoolean.OFF;
			} else if (code == 2) {
				return DoubleBoolean.ON;
			} else if (code == 3) {
				return DoubleBoolean.INDETERMINACY2;
			}else {
				throw new IllegalArgumentException();
			}
		}

	}


	@AllArgsConstructor
	public enum CommandType {
		/**
		 * 选择
		 */
		CHOICE(1, "选择"),
		/**
		 * 执行
		 */
		EXECUTE(0, "执行");

		private Integer code;

		private String msg;

		public static CommandType valueOf(Integer code) {
			if (code == 0) {
				return CommandType.EXECUTE;
			} else if (code == 1) {
				return CommandType.CHOICE;
			} else {
				throw new IllegalArgumentException();
			}
		}
	}


	@AllArgsConstructor
	public enum ControlProperty {

		UNDEFINED(0, "无定义"),
		SHORT_PULSE_DURATION(1, "短脉冲持续时间"),
		LONG_PULSE_DURATION(2, "长脉冲持续时间"),
		CONTINUOUS_OUTPUT(3, "持续输出");

		private Integer code;

		private String msg;

		public static ControlProperty valueOf(Integer code) {
			switch (code) {
				case 0:
					return ControlProperty.UNDEFINED;
				case 1:
					return ControlProperty.SHORT_PULSE_DURATION;
				case 2:
					return ControlProperty.LONG_PULSE_DURATION;
				case 3:
					return ControlProperty.CONTINUOUS_OUTPUT;
				default:
					throw new IllegalArgumentException();
			}
		}
	}
}
