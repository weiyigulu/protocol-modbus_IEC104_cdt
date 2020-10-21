import wei.yigulu.cdt.netty.CDTMaster;

/**
 * @author 修唯xiuwei
 * @create 2019-06-26 13:44
 * @Email 524710549@qq.com
 **/
public class TestMaster {
	public static void main(String[] args) throws InterruptedException {
		new Thread(() -> {
			new CDTMaster("COM1", new LocalCDTDataHandler()).create();
		}).start();
       /*for (;;){
           System.out.println(DataContainer.getInstance().toString());
           Thread.sleep(3000);
       }*/
	}
}
