package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;

import java.util.List;

/**
 * 数据帧的简单实现类 方便继承者简单实现
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class SimpleDataFrameType extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = -1;

	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {

	}

	@Override
	public void encode(List<Byte> buffer) {

	}

	@Override
	public Asdu generateBack() {
		return null;
	}

	@Override
	public byte[][] handleAndAnswer(Apdu apdu) throws Exception {
		return new byte[0][];
	}
}
