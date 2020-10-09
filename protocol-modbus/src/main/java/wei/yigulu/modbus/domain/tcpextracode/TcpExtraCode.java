package wei.yigulu.modbus.domain.tcpextracode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * TCP通讯时 数据帧开头的附加码
 *
 * @author: xiuwei
 * @version:
 */
@Accessors(chain = true)
public class TcpExtraCode {


	/**
	 * 事务标识符
	 */
	@Setter
	@Getter
	private TransactionIdentifier transactionIdentifier = new TransactionIdentifier();

	/**
	 * 协议标识符
	 */
	@Setter
	@Getter
	private ProtocolIdentification protocolIdentification = new ProtocolIdentification();


	public void encode(List<Byte> bytes) {
		transactionIdentifier.encode(bytes);
		protocolIdentification.encode(bytes);
	}

	public void decode(ByteBuffer byteBuf) {
		this.transactionIdentifier.decode(byteBuf);
		this.protocolIdentification.decode(byteBuf);
	}

}


