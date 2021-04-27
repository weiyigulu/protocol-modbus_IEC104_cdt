package wei.yigulu.modbus.domain;

import wei.yigulu.modbus.domain.response.AbstractModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @program: protocol
 * @description: Modbus 报文接口
 * @author: xiuwei
 * @create: 2021-04-27 11:27
 */
public interface ModbusPacketInterface {


	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲区
	 * @return {@link AbstractModbusResponse}* @throws ModbusException modbus例外
	 */
	ModbusPacketInterface decode(ByteBuffer byteBuf) throws ModbusException;


	/**
	 * 编码
	 *
	 * @param bytes 字节
	 */
	ModbusPacketInterface encode(List<Byte> bytes) throws ModbusException;
}
