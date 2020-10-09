package wei.yigulu.modbus.domain.tcpextracode;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 协议标识符
 *
 * @author xiuwei
 */
@Data
@Accessors(chain = true)
public class ProtocolIdentification {

	/**
	 * 通讯时的附加码3 协议标识符1
	 */
	private Byte extraCode3 = 0;

	/**
	 * 通讯时的附加码4 协议标识符2
	 */
	private Byte extraCode4 = 0;


	/**
	 * 编码
	 *
	 * @param bytes
	 */
	public void encode(List<Byte> bytes) {
		bytes.add(extraCode3);
		bytes.add(extraCode4);
	}

	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲区
	 */
	public void decode(ByteBuffer byteBuf) {
		extraCode3 = byteBuf.get();
		extraCode4 = byteBuf.get();
	}

}
