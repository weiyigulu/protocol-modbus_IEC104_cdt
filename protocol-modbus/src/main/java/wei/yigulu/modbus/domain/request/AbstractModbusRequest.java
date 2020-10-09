package wei.yigulu.modbus.domain.request;


import lombok.Data;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * modbus请求数据报文的数据帧的抽象类
 *
 * @author: xiuwei
 * @version:
 */
@Data
@Accessors(chain = true)
public class AbstractModbusRequest {


	/**
	 * 客户端地址 一字节
	 */
	protected Integer slaveId = 01;

	/**
	 * 功能码  一字节
	 */
	protected Integer functionCode = 03;

	/**
	 * 请求数据的起始地址位  两字节
	 */
	protected Integer startAddress;

	/**
	 * 请求的寄存器的数量  两字节
	 */
	protected Integer quantity;

	/**
	 * 编码
	 *
	 * @param bytes 字节
	 */
	public void encode(List<Byte> bytes) {
		bytes.add((byte) (slaveId & 0xff));
		bytes.add((byte) (functionCode & 0xff));
		new P_AB(BigDecimal.valueOf(startAddress)).encode(bytes);
		new P_AB(BigDecimal.valueOf(quantity)).encode(bytes);
	}

	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲
	 */
	public AbstractModbusRequest decode(ByteBuffer byteBuf) throws ModbusException {
		this.setSlaveId(byteBuf.get() & 0xff);
		this.setFunctionCode(byteBuf.get() & 0xff);
		this.setStartAddress(new P_AB().decode(byteBuf).getValue().intValue());
		this.setQuantity(new P_AB().decode(byteBuf).getValue().intValue());
		return this;
	}


}