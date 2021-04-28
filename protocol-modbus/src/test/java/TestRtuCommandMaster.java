import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.numeric.ABCD;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.synchronouswaitingroom.TcpSynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusRtuMasterBuilder;
import wei.yigulu.modbus.netty.ModbusTcpMasterBuilder;
import wei.yigulu.modbus.utils.ModbusCommandDataUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestRtuCommandMaster {
	public static void main(String[] args) throws InterruptedException, ModbusException {
		ModbusRtuMasterBuilder master = new ModbusRtuMasterBuilder("COM1");
		master.createByUnBlock();
		TcpSynchronousWaitingRoom.waitTime=5000L;
		Thread.sleep(3000L);
		List<RegisterValue> list = new ArrayList<>();
		for (int i = 0; i <= 00; i++) {
			list.add(new P_AB().setValue(BigDecimal.valueOf(2*i)));
		}
		ModbusCommandDataUtils.commandRegister(master,1,0,list);
		Thread.sleep(30L);
		}


}
