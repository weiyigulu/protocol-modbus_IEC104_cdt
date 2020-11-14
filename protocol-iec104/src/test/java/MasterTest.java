import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.asdudataframe.TotalSummonType;
import wei.yigulu.iec104.nettyconfig.Iec104HSMasterBuilder;
import wei.yigulu.iec104.nettyconfig.Iec104MasterBuilder;

/**
 * dad
 *
 * @author 修唯xiuwei
 * @create 2019-03-14 16:46
 * @Email 524710549@qq.com
 **/
public class MasterTest {

	public static void main(String[] args) throws Exception {

		Iec104MasterBuilder masterBuilder = new Iec104MasterBuilder("127.0.0.1", 24004);
		masterBuilder.createByUnBlock();

	/*	//创建总召唤类型I帧
		TotalSummonType totalSummonType = new TotalSummonType();
		//反向生成asdu
		Asdu asdu = totalSummonType.generateBack();
		//配置总召唤发送原因
		asdu.setNot(6);
		//配置公共地址位
		asdu.setCommonAddress(1);
		Apdu apdu = new Apdu().setAsdu(asdu);
		masterBuilder.sendFrameToOpposite(apdu.encode());*/

	}
}
