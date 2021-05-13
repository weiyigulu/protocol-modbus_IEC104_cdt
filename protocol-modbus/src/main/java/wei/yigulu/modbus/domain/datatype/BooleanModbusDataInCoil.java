package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 8个布尔值组成的数据类型
 * 仅对应 01 和 02 功能码
 *
 * @author: xiuwei
 * @version:
 */
public class BooleanModbusDataInCoil extends CoilValue {

	public static final int BIT_NUM = 8;

	/**
	 * 八个布尔 对应一字节 八比特
	 */
	boolean[] values = new boolean[BIT_NUM];


	@Override
	public IModbusDataType decode(byte[] bytes, int offset) {
		return getValFormByte(bytes[offset]);
	}

	@Override
	public IModbusDataType decode(ByteBuffer byteBuf) {
		return getValFormByte(byteBuf.get());

	}

	@Override
	public BooleanModbusDataInCoil encode(List<Byte> bytes) {
		byte b = 0;
		for (int i = 0; i < values.length; i++) {
			if (this.values[i]) {
				b = (byte) (b | 0x01 >> i);
			}
		}
		bytes.add(b);
		return this;
	}

	private IModbusDataType getValFormByte(byte b) {
		for (int i = 0; i < values.length; i++) {
			this.values[i] = (byte) ((b >> i) & 0x01) == 0x01;
		}
		return this;
	}


	public void setValue(int index, boolean value) {
		if (index < values.length) {
			this.values[index] = value;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean getValue(int index) {
		if (index < values.length) {
			return this.values[index];
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * 通过布尔list 生成 List<BooleanModbusDataInCoil>
	 *
	 * @param booleanValues 布尔值
	 * @return {@link List<BooleanModbusDataInCoil>}
	 */
	public static List<BooleanModbusDataInCoil> getFormBooleanList(List<Boolean> booleanValues) {
		int size = Double.valueOf(Math.ceil(booleanValues.size() / BIT_NUM)).intValue();
		List<BooleanModbusDataInCoil> booleanModbusDataInCoils = new ArrayList<>();
		BooleanModbusDataInCoil booleanModbusDataInCoil;
		int index;
		for (int i = 0; i < size; i++) {
			booleanModbusDataInCoil = new BooleanModbusDataInCoil();
			for (int j = 0; j < BIT_NUM; j++) {
				index = BIT_NUM * i + j;
				if (booleanValues.size() > index) {
					booleanModbusDataInCoil.setValue(j, booleanValues.get(index));
				} else {
					booleanModbusDataInCoils.add(booleanModbusDataInCoil);
					break;
				}
				booleanModbusDataInCoils.add(booleanModbusDataInCoil);
			}
		}
		return booleanModbusDataInCoils;
	}


}
