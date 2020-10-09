import wei.yigulu.modbus.domain.datatype.numeric.ABCD;
import wei.yigulu.modbus.netty.ModbusTcpSlaverBuilder;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author: xiuwei
 * @version:
 */
public class TestSlaver {
	public static void main(String[] args) throws InterruptedException {
		ModbusTcpSlaverBuilder slaverBuilder = new ModbusTcpSlaverBuilder(2409);
		slaverBuilder.createByUnBlock();

		Random random = new Random();
		for (; ; ) {
			for (int i = 0; i < 10; i++) {
				slaverBuilder.getModbusSlaveDataContainer().setRegister(1, i, new ABCD(BigDecimal.valueOf((0.5 - random.nextDouble()) * 100)));
			}
			Thread.sleep(200L);
		}
	}
}
