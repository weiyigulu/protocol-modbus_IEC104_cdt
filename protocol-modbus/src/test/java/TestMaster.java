import lombok.extern.slf4j.Slf4j;
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


		ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 5025);
		master.createByUnBlock();
		Thread.sleep(3000L);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
		for (int i = 0; i <= 7; i++) {
			map.put(i * 2, ModbusDataTypeEnum.P_CDAB);
		}
		List<ModbusRequestDataUtils.Obj4RequestData> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, 3);

		for (; ; ) {
			try {
				Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getData(master, ll);
				for (Integer i : map1.keySet()) {
					System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(3000L);
		}


	}
}
