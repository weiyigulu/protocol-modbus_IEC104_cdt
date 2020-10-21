package wei.yigulu.modbus.domain;

import lombok.Getter;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.request.RtuModbusRequest;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.Map;

/**
 * 请求寄存器的辅助类
 *
 * @author: xiuwei
 * @version:
 */
public class Obj4RequestRegister extends Obj4RequestData {

	@Getter
	Map<Integer, ModbusDataTypeEnum> locator;

	public Obj4RequestRegister(int slaveId, FunctionCode functionCode, Map<Integer, ModbusDataTypeEnum> locator) throws ModbusException {
		super(slaveId, functionCode);
		if (functionCode != FunctionCode.READ_HOLDING_REGISTERS && functionCode != FunctionCode.READ_INPUT_REGISTERS) {
			throw new ModbusException("该实体仅能接受3，4功能码，请求寄存器数据");
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
