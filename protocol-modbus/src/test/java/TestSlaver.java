import wei.yigulu.modbus.domain.datatype.numeric.BADC;
import wei.yigulu.modbus.netty.ModbusTcpSlaverBuilder;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author: xiuwei
 * @version:
 */
public class TestSlaver {
	public static void main(String[] args) throws InterruptedException {
		ModbusTcpSlaverBuilder slaverBuilder = new ModbusTcpSlaverBuilder(502);
		slaverBuilder.createByUnBlock();

		Random random = new Random();
		boolean f;
		for (; ; ) {
			for (int i = 0; i < 10; i++) {
				slaverBuilder.getModbusSlaveDataContainer().setRegister(1, i, new BADC(BigDecimal.valueOf(random.nextFloat())));
				/*f = random.nextBoolean();
				System.out.println(i + ":" + f);
				slaverBuilder.getModbusSlaveDataContainer().setCoil(1, i, f);*/
			}
			Thread.sleep(2000L);
		}
	}
}
