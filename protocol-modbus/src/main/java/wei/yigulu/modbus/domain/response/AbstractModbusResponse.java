package wei.yigulu.modbus.domain.response;

import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.Setter;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusPacketInterface;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * modbus的响应报文 包含数据的 报文 的抽象类
 *
 * @author: xiuwei
 * @version:
 */
@Setter
@Getter
public class AbstractModbusResponse implements ModbusPacketInterface {


	/**
	 * 设备号  1字节
	 */
	protected Integer slaveId;

	/**
	 * 功能码 1字节
	 */
	protected FunctionCode functionCode;

	/**
	 * 表示数据的 字节 数量  1字节
	 */
	protected Integer dataBitNum;

	/**
	 * 用于表示数据的字节
	 */
	protected byte[] dataBytes;


	@Override
	public AbstractModbusResponse decode(ByteBuffer byteBuf) throws ModbusException {
		this.slaveId = (int) byteBuf.get() & 0xff;
		this.functionCode = FunctionCode.valueOf((int) byteBuf.get() & 0xff);
		this.dataBitNum = (int) byteBuf.get() & 0xff;
		this.dataBytes = new byte[this.dataBitNum];
		byteBuf.get(this.dataBytes);
		return this;
	}

	@Override
	public AbstractModbusResponse encode(List<Byte> bytes) throws ModbusException {
		bytes.add((byte) (slaveId & 0xff));
		bytes.add((byte) (functionCode.getCode() & 0xff));
		bytes.add((byte) (dataBytes.length & 0xff));
		bytes.addAll(Bytes.asList(dataBytes));
		return this;
	}


}
