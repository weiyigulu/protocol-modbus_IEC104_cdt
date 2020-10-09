import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.datatype.NumericModbusData;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusRtuMasterBuilder;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestRtuMaster {
	public static void main(String[] args) throws InterruptedException, ModbusException {
		ModbusRtuMasterBuilder master = new ModbusRtuMasterBuilder("COM1");
		master.createByUnBlock();

		ModbusRtuMasterBuilder master1 = new ModbusRtuMasterBuilder("COM3");
		master1.createByUnBlock();
		ModbusRtuMasterBuilder master2 = new ModbusRtuMasterBuilder("COM5");
		master2.createByUnBlock();
		ModbusRtuMasterBuilder master3 = new ModbusRtuMasterBuilder("COM7");
		master3.createByUnBlock();
		Thread.sleep(5000L);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
		for (int i = 0; i <= 30; i++) {
			map.put(i * 2, ModbusDataTypeEnum.ABCD);
		}
		List<ModbusRequestDataUtils.Obj4RequestData> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, 3);

		for (; ; ) {
			try {
				Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getData(master, ll);
				for (Integer i : map1.keySet()) {
					System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
				}
			} catch (ModbusException e) {
				System.out.println(e.getMsg());
			}
			Thread.sleep(3000L);
		}


	}
}
