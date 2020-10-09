package wei.yigulu.modbus.domain.response;

import com.google.common.primitives.Bytes;
import wei.yigulu.modbus.domain.datatype.numeric.P_BA;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.utils.CrcUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * modbus RTU 通讯时的响应报文
 *
 * @author: xiuwei
 * @version:
 */
public class RtuModbusResponse extends AbstractModbusResponse {

	/**
	 * crc 校验  两位 除去本两位 其余所有字节的校验位
	 */
	protected Integer crc16;


	@Override
	public AbstractModbusResponse decode(ByteBuffer byteBuf) throws ModbusException {
		if (byteBuf.remaining() == 5) {
			if (byteBuf.get(2) == 0x01) {
				throw new ModbusException(3101, "错误帧,slave不支持所请求的功能码");
			} else if (byteBuf.get(2) == 0x02) {
				throw new ModbusException(3101, "错误帧,slave没有请求的数量的数据");
			}
		}
		super.decode(byteBuf);
		return this;
	}

	@Override
	public AbstractModbusResponse encode(List<Byte> bytes) throws ModbusException {
		super.encode(bytes);
		crc16 = CrcUtils.generateCRC16(Bytes.toArray(bytes));
		new P_BA(BigDecimal.valueOf(crc16)).encode(bytes);
		return this;
	}

}
