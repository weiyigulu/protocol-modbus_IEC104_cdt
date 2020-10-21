import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestRegister;
import wei.yigulu.modbus.domain.datatype.BooleanModbusDataInRegister;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.datatype.NumericModbusData;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusTcpMasterBuilder;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestMaster {
	public static void main(String[] args) throws InterruptedException, ModbusException {


		ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 502);
		master.createByUnBlock();
		Thread.sleep(3000L);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
		for (int i = 0; i <= 7; i++) {
			map.put(i * 2, ModbusDataTypeEnum.P_CDAB);
		}
		map.put(18,ModbusDataTypeEnum.A16);
		List<Obj4RequestRegister> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, FunctionCode.READ_HOLDING_REGISTERS);

		for (; ; ) {
			try {
				Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getRegisterData(master, ll);
				for (Integer i : map1.keySet()) {
					if(map1.get(i) instanceof  NumericModbusData) {
						System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
					}else {
						System.out.println(i + " ============ " + JSON.toJSONString(((BooleanModbusDataInRegister) map1.get(i)).getValues()));
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(3000L);
		}


	}
}
