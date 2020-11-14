/**
 * @author: xiuwei
 * @version:
 */

import wei.yigulu.iec104.annotation.AsduType;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.asdudataframe.TotalSummonType;
import wei.yigulu.iec104.util.SendDataFrameHelper;

import java.util.HashMap;
import java.util.Map;

@AsduType
public class TestTotalS extends TotalSummonType {

	@Override
	public byte[][] handleAndAnswer(Apdu apdu) throws Exception {
		Map<Integer,Boolean> booleans=new HashMap<>();
		for(int i =0 ;i<100;i++){
			booleans.put(i,true);
		}
		SendDataFrameHelper.sendYxDataFrame(apdu.getChannel(),booleans,1,20,null);
		return null;
	}

}
