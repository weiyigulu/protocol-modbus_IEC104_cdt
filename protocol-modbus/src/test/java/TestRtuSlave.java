import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.datatype.numeric.P_ABCD;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusRtuSlaverBuilder;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TestRtuSlave {
	public static void main(String[] args) throws InterruptedException, ModbusException {


		ModbusRtuSlaverBuilder slaver = new ModbusRtuSlaverBuilder("COM1");
		slaver.createByUnBlock();
		Thread.sleep(3000L);
		Random random = new Random();
		double d;
		for (; ; ) {
			for (int i = 0; i < 10; i++) {
				d = (0.5 - random.nextDouble()) * 100;
				System.out.println(d);
				//slaver.getModbusSlaveDataContainer().setRegister(1, i * 2, new P_ABCD(BigDecimal.valueOf(d)));
			}
			Thread.sleep(1000000L);
		}


	}
}
