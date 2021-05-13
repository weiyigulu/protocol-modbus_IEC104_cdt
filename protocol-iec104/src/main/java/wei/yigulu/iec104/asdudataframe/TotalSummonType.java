package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;

import java.util.List;

/**
 * 总召唤帧
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalSummonType extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.TOTAL_SUMMONTYPE_TYPE;

	private InformationBodyAddress address = new InformationBodyAddress(20);

	private int value;


	@Override
	public void encode(List<Byte> buffer) {
		address.encode(buffer);
		buffer.add((byte) value);
	}

	@Override
	public Asdu generateBack() {
		Asdu asdu = new Asdu();
		asdu.setTypeId(100);
		asdu.setDataFrame(this);
		asdu.getVsq().setNum(1);
		asdu.getVsq().setSq(0);
		asdu.getCot().setNot(7);
		asdu.setOriginatorAddress(0);
		asdu.setCommonAddress(1);
		return asdu;
	}


	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {
		try {
			this.address = new InformationBodyAddress(is);
			this.value = (is.readByte() & 0xff);
		} catch (Iec104Exception e) {
			if (e.getCode() == 3301) {
				return;
			}
		}
	}

	@Override
	public byte[][] handleAndAnswer(Apdu apdu) throws Exception {
		return null;
	}


	@Override
	public String toString() {
		String s = address.toString();
		return "总召唤令；" + s + "\n召唤值：" + value + "\n";
	}

}
