package wei.yigulu.modbus.domain.confirm;

import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusPacketInterface;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: protocol
 * @description: modbus的控制命令
 * @author: xiuwei
 * @create: 2021-04-26 16:14
 */
public abstract class AbstractModbusConfirm implements ModbusPacketInterface {


	/**
	 * 客户端地址 一字节
	 */
	@Setter
	@Accessors(chain = true)
	protected Integer slaveId = 01;

	/**
	 * 功能码  一字节  5，6，15，16
	 */
	protected FunctionCode functionCode;

	/**
	 * 下达数据的起始地址位  两字节
	 */
	protected Integer startAddress;

	/**
	 * 输出数据的数量（15，16） 或数据值（5，6）  两字节
	 */
	protected Integer quantity;

	public Integer getLength() {
		return 8;
	}


	@Override
	public AbstractModbusConfirm encode(List<Byte> bytes) throws ModbusException {
		bytes.add((byte) (slaveId & 0xff));
		bytes.add((byte) (functionCode.getCode() & 0xff));
		new P_AB(BigDecimal.valueOf(startAddress)).encode(bytes);
		new P_AB(BigDecimal.valueOf(quantity)).encode(bytes);
		return this;
	}


}
