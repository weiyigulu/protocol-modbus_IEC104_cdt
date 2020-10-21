package wei.yigulu.modbus.domain;

import lombok.Getter;
import wei.yigulu.modbus.domain.request.RtuModbusRequest;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.List;

/**
 * 请求寄存器的辅助类
 *
 * @author: xiuwei
 * @version:
 */
public class Obj4RequestCoil extends Obj4RequestData {

	@Getter
	List<Integer> locator;

	public Obj4RequestCoil(int slaveId, FunctionCode functionCode, List<Integer> locator) throws ModbusException {
		super(slaveId, functionCode);
		if (FunctionCode.READ_COILS != functionCode && FunctionCode.READ_DISCRETE_INPUTS != functionCode) {
			throw new ModbusException("该实体仅能接受1，2功能码，请求线圈数据");
		}
		this.locator = locator;
	}

	@Override
	public TcpModbusRequest getTcpModbusRequest() throws ModbusException {
		if (this.tcpModbusRequest == null) {
			this.tcpModbusRequest = ModbusRequestDataUtils.verifyAndCreateRequest(new TcpModbusRequest(), slaveId, functionCode, locator);
		}
		return this.tcpModbusRequest;
	}

	@Override
	public RtuModbusRequest getRtuModbusRequest() throws ModbusException {
		if (this.rtuModbusRequest == null) {
			this.rtuModbusRequest = ModbusRequestDataUtils.verifyAndCreateRequest(new RtuModbusRequest(), slaveId, functionCode, locator);
		}
		return this.rtuModbusRequest;
	}
}
