package wei.yigulu.modbus.domain.command;

import lombok.Getter;
import lombok.Setter;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.tcpextracode.TcpExtraCode;
import wei.yigulu.modbus.domain.tcpextracode.TransactionIdentifier;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * tcp写的modbus 控制命令
 *
 * @author: xiuwei
 * @version:
 */

public class TcpModbusCommand extends AbstractModbusCommand {
	/**
	 * tcp通讯时的前端附加码
	 */
	@Setter
	@Getter
	protected TcpExtraCode tcpExtraCode = new TcpExtraCode();
	/**
	 * 除去四个附加码 和两个长度字节 剩余的报文的字节个数
	 */
	@Setter
	protected Integer length = 6;

	/**
	 * 设置事务标识符
	 *
	 * @param transactionIdentifier
	 * @return
	 */
	public TcpModbusCommand setTransactionIdentifier(TransactionIdentifier transactionIdentifier) {
		this.tcpExtraCode.setTransactionIdentifier(transactionIdentifier);
		return this;
	}

	@Override
	public TcpModbusCommand encode(List<Byte> bytes) throws ModbusException {
		tcpExtraCode.encode(bytes);
		new P_AB(BigDecimal.valueOf(super.getLength())).encode(bytes);
		super.encode(bytes);
		return this;
	}


	@Override
	public TcpModbusCommand decode(ByteBuffer byteBuf) throws ModbusException {
		this.tcpExtraCode.decode(byteBuf);
		this.setLength(new P_AB().decode(byteBuf).getValue().intValue());
		super.decode(byteBuf);
		return this;
	}

}
