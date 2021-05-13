import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestCoil;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusTcpMasterBuilder;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestMasterCoil {
	public static void main(String[] args) throws InterruptedException, ModbusException {


		ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 502);
		master.createByUnBlock();
		Thread.sleep(3000L);
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i <= 7; i++) {
			list.add(i);
		}
		list.add(30);
		List<Obj4RequestCoil> ll = ModbusRequestDataUtils.splitModbusRequest(list, 1, FunctionCode.READ_COILS);

		for (; ; ) {
			try {
				Map<Integer, Boolean> map1 = ModbusRequestDataUtils.getCoilData(master, ll);
				ArrayList<Integer> lll = new ArrayList<Integer>(map1.keySet());
				Collections.sort(lll);
				for (Integer i : lll) {
					System.out.println(i + " ============ " + map1.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(3000L);
		}


	}
}
