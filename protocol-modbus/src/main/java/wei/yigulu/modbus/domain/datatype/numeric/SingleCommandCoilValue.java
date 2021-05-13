package wei.yigulu.modbus.domain.datatype.numeric;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import wei.yigulu.modbus.domain.datatype.CoilValue;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 命令报文中的coil设定值
 *
 * @author: xiuwei
 * @vesion:
 */
@AllArgsConstructor
@NoArgsConstructor
public class SingleCommandCoilValue extends CoilValue {

	boolean bValue;


	@Override
	public IModbusDataType decode(byte[] bytes, int offset) {
		return null;
	}

	@Override
	public IModbusDataType decode(ByteBuffer byteBuf) {
		return null;
	}

	@Override
	public IModbusDataType encode(List<Byte> bytes) {
		if (bValue) {
			bytes.add((byte) 0xff);
		} else {
			bytes.add((byte) 0x00);
		}
		bytes.add((byte) 0x00);
		return this;
	}


	public byte[] encode() {
		byte[] bs = new byte[2];
		if (bValue) {
			bs[0] = (byte) 0xff;
		} else {
			bs[0] = (byte) 0x00;
		}
		bs[1] = (byte) 0x00;
		return bs;
	}
}
