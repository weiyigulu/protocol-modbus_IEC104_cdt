package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 8个布尔值组成的数据类型
 * 仅对应 01 和 02 功能码
 *
 * @author: xiuwei
 * @version:
 */
public class BooleanModbusDataInCoil extends CoilValue {

	/**
	 * 八个布尔 对应一字节 八比特
	 */
	boolean[] values = new boolean[8];


	@Override
	public IModbusDataType decode(byte[] bytes, int offset) {
		return getValFormByte(bytes[offset]);
	}

	@Override
	public IModbusDataType decode(ByteBuffer byteBuf) {
		return getValFormByte(byteBuf.get());

	}

	private IModbusDataType getValFormByte(byte b) {
		for (int i = 0; i < values.length; i++) {
			this.values[i] = (byte) ((b >> i) & 0x01) == 0x01;
		}
		return this;
	}


	public void setValue(int index,boolean value){
		if(index<values.length){
			this.values[index]=value;
		}else{
			throw new IllegalArgumentException();
		}
	}

	public boolean getValue(int index){
		if(index<values.length){
			return this.values[index];
		}else{
			throw new IllegalArgumentException();
		}
	}


}
