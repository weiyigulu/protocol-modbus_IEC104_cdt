package wei.yigulu.modbus.utils;

import com.google.common.primitives.Bytes;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusSlaveDataContainer;
import wei.yigulu.modbus.domain.request.AbstractModbusRequest;
import wei.yigulu.modbus.domain.response.AbstractModbusResponse;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.util.ArrayList;
import java.util.List;

/**
 * modbus响应数据时的工具
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusResponseDataUtils {

	public static byte[] buildResponse(ModbusSlaveDataContainer modbusSlaveDataContainer, AbstractModbusRequest request, AbstractModbusResponse response) throws Exception {
		try {
			response.setDataBytes(modbusSlaveDataContainer.getDataBytesOfSlave(request));
			response.setSlaveId(request.getSlaveId());
			response.setFunctionCode(request.getFunctionCode());
		} catch (ModbusException e) {
			if (e.getCode() != null) {
				if (e.getCode() == 3004) {
					response.setSlaveId(request.getSlaveId());
					response.setFunctionCode(FunctionCode.valueOf(request.getFunctionCode().getCode() + 0x80));
					response.setDataBitNum(0x01);
				}
			}
			throw e;
		} catch (Exception e) {
			throw e;
		}
		List<Byte> bs = new ArrayList<>();
		response.encode(bs);
		byte[] bbs = Bytes.toArray(bs);
		return bbs;
	}


}
