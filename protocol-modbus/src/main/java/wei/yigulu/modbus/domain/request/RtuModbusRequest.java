package wei.yigulu.modbus.domain.request;

import com.google.common.primitives.Bytes;
import wei.yigulu.modbus.domain.datatype.numeric.P_BA;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.utils.CrcUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * RTU 通讯所用的请求报文
 *
 * @author: xiuwei
 * @version:
 */
public class RtuModbusRequest extends AbstractModbusRequest {

	/**
	 * crc 校验  两位 除去本两位 其余所有字节的校验位
	 */
	protected Integer crc16;

	/**
	 * 编码
	 *
	 * @param bytes 字节
	 * @return
	 */
	@Override
	public RtuModbusRequest encode(List<Byte> bytes) {
		super.encode(bytes);
		this.crc16 = CrcUtils.generateCRC16(Bytes.toArray(bytes));
		new P_BA(BigDecimal.valueOf(this.crc16)).encode(bytes);
		return this;
	}

	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲
	 */
	@Override
	public RtuModbusRequest decode(ByteBuffer byteBuf) throws ModbusException {
		super.decode(byteBuf);
		return this;
	}


}
