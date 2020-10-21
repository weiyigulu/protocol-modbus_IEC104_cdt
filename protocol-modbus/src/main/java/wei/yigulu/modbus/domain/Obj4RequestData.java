package wei.yigulu.modbus.domain;

import wei.yigulu.modbus.domain.request.RtuModbusRequest;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.exceptiom.ModbusException;

/**
 * 方便请求和解析数据所构建的对象
 *
 * @author Administrator
 */
public abstract class Obj4RequestData {


	int slaveId;

	FunctionCode functionCode;

	TcpModbusRequest tcpModbusRequest = null;

	RtuModbusRequest rtuModbusRequest = null;


	public Obj4RequestData(int slaveId, FunctionCode functionCode) {
		this.slaveId = slaveId;
		this.functionCode = functionCode;
	}


	public abstract TcpModbusRequest getTcpModbusRequest() throws ModbusException;

	public abstract RtuModbusRequest getRtuModbusRequest() throws ModbusException;

}
