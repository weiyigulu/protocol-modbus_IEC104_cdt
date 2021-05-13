package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.typemodel.IeProofreadTime;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;

import java.util.List;

/**
 * 时间校对帧
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProofreadTimeType extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.DATESYNCHRONIZATION_TYPE;

	private InformationBodyAddress address;

	private IeProofreadTime ieProofreadTime;


	@Override
	public void encode(List<Byte> buffer) {
		address.encode(buffer);
		ieProofreadTime.encode(buffer);
	}

	@Override
	public Asdu generateBack() {
		Asdu asdu = new Asdu();
		asdu.setTypeId(103);
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
			address = new InformationBodyAddress(is);
			ieProofreadTime = new IeProofreadTime(is);
		} catch (Iec104Exception e) {
			if (e.getCode() == 3301) {
				return;
			}
		}
	}

	@Override
	public byte[][] handleAndAnswer(Apdu Apdu) {
		return null;
	}

	@Override
	public String toString() {
		String s = address.toString() + "\n";
		s += "时间校对帧,时间为：" + ieProofreadTime.toString();
		return s;
	}


}
