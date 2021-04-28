import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.Obj4RequestRegister;
import wei.yigulu.modbus.domain.datatype.IModbusDataType;
import wei.yigulu.modbus.domain.datatype.ModbusDataTypeEnum;
import wei.yigulu.modbus.domain.datatype.NumericModbusData;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.numeric.ABCD;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.domain.synchronouswaitingroom.TcpSynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusTcpMasterBuilder;
import wei.yigulu.modbus.utils.ModbusCommandDataUtils;
import wei.yigulu.modbus.utils.ModbusRequestDataUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestTcpCommandMaster {
	public static void main(String[] args) throws InterruptedException, ModbusException {
		ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 5002);
		master.createByUnBlock();
		TcpSynchronousWaitingRoom.waitTime=5000L;
		Thread.sleep(3000L);
		List<RegisterValue> list = new ArrayList<>();
		for (int i = 0; i <= 0; i++) {
			list.add(new P_AB().setValue(BigDecimal.valueOf(5)));
		}
		ModbusCommandDataUtils.commandRegister(master,1,0,list);
		Thread.sleep(30L);
		}


}
