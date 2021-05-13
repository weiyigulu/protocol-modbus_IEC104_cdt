import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestRegister;
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
		master.setBaudRate(9600);
		master.createByUnBlock();
/*
		ModbusRtuMasterBuilder master1 = new ModbusRtuMasterBuilder("COM3");
		master1.createByUnBlock();
		ModbusRtuMasterBuilder master2 = new ModbusRtuMasterBuilder("COM5");
		master2.createByUnBlock();
		ModbusRtuMasterBuilder master3 = new ModbusRtuMasterBuilder("COM7");
		master3.createByUnBlock();*/
		Thread.sleep(5000L);
		Map<Integer, ModbusDataTypeEnum> map = new HashMap<>();
		for (int i = 0; i < 60; i += 2) {
			map.put(i, ModbusDataTypeEnum.CDAB);
		}
		List<Obj4RequestRegister> ll = ModbusRequestDataUtils.splitModbusRequest(map, 1, FunctionCode.READ_HOLDING_REGISTERS);

		for (; ; ) {
			try {
				Map<Integer, IModbusDataType> map1 = ModbusRequestDataUtils.getRegisterData(master, ll);
				for (Integer i : map1.keySet()) {
					System.out.println(i + " ============ " + ((NumericModbusData) map1.get(i)).getValue());
				}
			} catch (ModbusException e) {
				System.out.println(e.getMsg());
			}
			Thread.sleep(1000L);
		}


	}
}
