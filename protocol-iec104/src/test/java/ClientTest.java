import wei.yigulu.iec104.nettyconfig.Iec104HSMasterBuilder;


/**
 * 客户端测试
 *
 * @author 修唯xiuwei
 * @create 2019-01-22 16:05
 * @Email 524710549@qq.com
 **/
public class ClientTest {

	public static void main(String[] args) {
		new Iec104HSMasterBuilder("127.0.0.1", 2404).create();
		System.out.println(123);
	}

}
