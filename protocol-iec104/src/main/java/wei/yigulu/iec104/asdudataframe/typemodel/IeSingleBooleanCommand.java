package wei.yigulu.iec104.asdudataframe.typemodel;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 布尔的命令值，针对单点信号
 *
 * @author: xiuwei
 * @version:
 */
public class IeSingleBooleanCommand implements IecDataInterface {

	public IeSingleBooleanCommand(boolean val){
		this.val=val;
	}


	public IeSingleBooleanCommand(ByteBuf is){
		int b=is.readUnsignedByte();
		this.val=(b&1)==1;
		this.commandType=CommandType.valueOf(b>>7&1);
		this.controlProperty=ControlProperty.valueOf((b&127)>>1);
	}


	boolean val=false;
	/**
	 * 命令类型
	 */
	CommandType commandType=CommandType.CHOICE;

	@AllArgsConstructor
	public enum CommandType{
		/**
		 * 选择
		 */
		CHOICE(1,"选择"),
		/**
		 * 执行
		 */
		EXECUTE(0,"执行");

		private Integer code;

		private String msg;

		public static CommandType valueOf(Integer code){
			if(code==0){
				return CommandType.EXECUTE;
			}else if(code==1){
				return CommandType.CHOICE;
			}else{
				throw new IllegalArgumentException();
			}
		}
	}


	/**
	 * 控制属性
	 */
	ControlProperty controlProperty=ControlProperty.UNDEFINED;

	@AllArgsConstructor
	public enum ControlProperty{

		UNDEFINED(0,"无定义"),
		SHORT_PULSE_DURATION(1,"短脉冲持续时间"),
		LONG_PULSE_DURATION(2,"长脉冲持续时间"),
		CONTINUOUS_OUTPUT(3,"持续输出");

		private Integer code;

		private String msg;

		public static ControlProperty valueOf(Integer code){
			switch (code){
				case 0:return ControlProperty.UNDEFINED;
				case 1:return  ControlProperty.SHORT_PULSE_DURATION;
				case 2:return ControlProperty.LONG_PULSE_DURATION;
				case 3:return ControlProperty.CONTINUOUS_OUTPUT;
				default:throw  new IllegalArgumentException();
			}
		}
	}

	/**
	 * Encode  编码
	 *
	 * @param buffer buffer
	 */
	public void encode(List<Byte> buffer) {
		int b=0;
		b=b|(val?1:0);
		b=b|(this.controlProperty.code<<1);
		b=b|(this.commandType.code<<7);
		buffer.add((byte)b);
	}



	@Override
	public Boolean getIecValue() {
		return val;
	}


	@Override
	public String toString(){
		String s;
		s="值："+val+";";
		s+="命令类型："+commandType.msg+";";
		s+="控制属性："+controlProperty.msg+";";
		return s;
	}
}
