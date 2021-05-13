package wei.yigulu.modbus.domain.request;


import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.tcpextracode.TcpExtraCode;
import wei.yigulu.modbus.domain.tcpextracode.TransactionIdentifier;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * tcp通讯所用的请求报文
 *
 * @author: xiuwei
 * @version:
 */
@Data
@Accessors(chain = true)
public class TcpModbusRequest extends AbstractModbusRequest {


	/**
	 * tcp通讯时的前端附加码
	 */
	@Setter
	protected TcpExtraCode tcpExtraCode = new TcpExtraCode();

	/**
	 * 设置事务标识符
	 *
	 * @param transactionIdentifier
	 * @return
	 */
	public TcpModbusRequest setTransactionIdentifier(TransactionIdentifier transactionIdentifier) {
		this.tcpExtraCode.setTransactionIdentifier(transactionIdentifier);
		return this;
	}


	/**
	 * 除去四个附加码 和两个长度字节 剩余的报文的字节个数
	 */
	protected Integer length = 6;


	@Override
	public TcpModbusRequest encode(List<Byte> bytes) {
		tcpExtraCode.encode(bytes);
		new P_AB(BigDecimal.valueOf(length)).encode(bytes);
		super.encode(bytes);
		return this;
	}


	@Override
	public TcpModbusRequest decode(ByteBuffer byteBuf) throws ModbusException {
		if (byteBuf.remaining() != 12) {
			throw new ModbusException("该帧非数据请求帧");
		}
		this.tcpExtraCode.decode(byteBuf);
		this.setLength(new P_AB().decode(byteBuf).getValue().intValue());
		super.decode(byteBuf);
		return this;
	}


}
