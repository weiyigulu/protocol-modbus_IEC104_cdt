import wei.yigulu.modbus.netty.ModbusTcpMasterBuilder;

/**
 * @author: xiuwei
 * @version:
 */
public class Test {
	public static void main(String[] args) throws InterruptedException {
		ModbusTcpMasterBuilder master = new ModbusTcpMasterBuilder("127.0.0.1", 5002);
		master.createByUnBlock();
		Thread.sleep(30000L);
		System.out.println("重启");
		master.stop();
		master.createByUnBlock();
	}
}
