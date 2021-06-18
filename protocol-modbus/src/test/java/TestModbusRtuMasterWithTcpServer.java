import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestRegister;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.datatype.NumericModbusData;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusRtuMasterWithTcpServer;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiuwei
 * @version:
 */
public class TestModbusRtuMasterWithTcpServer {
	public static void main(String[] args)  {
		try {
			ModbusRtuMasterWithTcpServer ss = new ModbusRtuMasterWithTcpServer(502);
			ss.createByUnBlock();
			Thread.sleep(5000L);
			Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
			for (int i = 0; i < 10; i += 2) {
				map.put(i, ModbusDataTypeEnum.CDAB);
			}
			List<Obj4RequestRegister> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, FunctionCode.READ_HOLDING_REGISTERS);
			for (; ; ) {
				try {
					Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getRegisterData(ss, ll);
					for (Integer i : map1.keySet()) {
						System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
					}
				} catch (ModbusException e) {
					System.out.println(e.getMsg());
				}
				Thread.sleep(1000L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
