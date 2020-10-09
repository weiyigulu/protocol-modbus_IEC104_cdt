package wei.yigulu.modbus.domain.response;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.tcpextracode.TcpExtraCode;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * modbus 的 tcp 响应帧
 *
 * @author: xiuwei
 * @version:
 */
@Getter
@Setter
@Slf4j
public class TcpModbusResponse extends AbstractModbusResponse {


	/**
	 * TCP通讯时报文前端的附加码
	 */
	protected TcpExtraCode tcpExtraCode = new TcpExtraCode();

	/**
	 * 除去四个附加码 和两个长度字节 剩余的报文的字节个数
	 */
	protected Integer length;


	@Override
	public TcpModbusResponse decode(ByteBuffer byteBuf) throws ModbusException {
		if (byteBuf.remaining() == 9) {
			if (byteBuf.get(8) == 0x01) {
				throw new ModbusException(3101, "错误帧,slave不支持所请求的功能码");
			} else if (byteBuf.get(8) == 0x02) {
				throw new ModbusException(3101, "错误帧,slave没有请求的数量的数据");
			}
		}
		tcpExtraCode.decode(byteBuf);
		length = new P_AB().decode(byteBuf).getValue().intValue();
		if (byteBuf.remaining() == length) {
			super.decode(byteBuf);
		} else {
			log.warn("数据帧长度：" + byteBuf.remaining() + ";规定长度：" + length);
			throw new ModbusException("数据帧实际长度与规定长度不符");
		}
		return this;
	}

	@Override
	public AbstractModbusResponse encode(List<Byte> bytes) throws ModbusException {
		List<Byte> son = new ArrayList<>();
		super.encode(son);
		tcpExtraCode.encode(bytes);
		new P_AB(BigDecimal.valueOf(son.size())).encode(bytes);
		bytes.addAll(son);
		return this;
	}


}
