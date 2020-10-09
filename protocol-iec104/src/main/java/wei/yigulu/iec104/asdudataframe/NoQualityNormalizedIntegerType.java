package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.typemodel.IeShortInteger;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;

import java.util.ArrayList;
import java.util.List;

/**
 * 没有品质位的归一化值
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Data
@NoArgsConstructor
public class NoQualityNormalizedIntegerType extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.NOQUALITY_NORMALIZED_INTEGER_TYPE;

	private List<InformationBodyAddress> addresses = new ArrayList<>();

	private List<Integer> datas = new ArrayList<>();

	/**
	 * No quality normalized integer type
	 *
	 * @param addresses addresses
	 * @param datas     datas
	 * @throws Iec104Exception iec exception
	 */
	public NoQualityNormalizedIntegerType(List<InformationBodyAddress> addresses, List<Integer> datas) throws Iec104Exception {
		if ((this.datas.size() * 3 + this.addresses.size() * 4) > 240) {
			throw new Iec104Exception("长度超长，创建对象失败，请切割数据。");
		}
		this.addresses = addresses;
		this.datas = datas;
	}


	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {
		Integer f;
		if (vsq.getSq() == 0) {
			for (int i = 0; i < vsq.getNum(); i++) {
				addresses.add(new InformationBodyAddress(is));
				f = new IeShortInteger(is).getValue();
				datas.add(f);
			}
		} else {
			addresses.add(new InformationBodyAddress(is));
			for (int i = 0; i < vsq.getNum(); i++) {
				f = new IeShortInteger(is).getValue();
				datas.add(f);
			}
		}
	}

	/**
	 * 向datas中添加数据，默认的质量描述
	 *
	 * @param f f
	 */
	public void addData(int f) {
		this.datas.add(f);
	}


	/**
	 * 向datas中添加数据和数据地址
	 *
	 * @param address address
	 * @param f       f
	 */
	public void addDataAndAdd(InformationBodyAddress address, int f) {
		this.addresses.add(address);
		addData(f);
	}


	/**
	 * 向datas中添加数据和数据地址
	 *
	 * @param address address
	 * @throws Iec104Exception iec exception
	 */
	public void addAddress(InformationBodyAddress address) throws Iec104Exception {
		validateLen(4);
		this.addresses.add(address);
	}


	@Override
	public void encode(List<Byte> buffer) {
		if (addresses.size() == 1) {
			addresses.get(0).encode(buffer);
			for (Integer i : datas) {
				new IeShortInteger(i).encode(buffer);
			}
		} else {
			int s = 0;
			for (Integer i : datas) {
				addresses.get(s++).encode(buffer);
				new IeShortInteger(i).encode(buffer);
			}
		}
	}

	@Override
	public Asdu generateBack() {
		Asdu asdu = new Asdu();
		asdu.setTypeId(11);
		asdu.setDataFrame(this);
		asdu.getVsq().setSq(this.addresses.size() == 1 ? 1 : 0);
		asdu.getVsq().setNum(this.datas.size());
		asdu.setOriginatorAddress(0);
		asdu.setCommonAddress(1);
		return asdu;
	}


	/**
	 * Validate len *
	 *
	 * @param increase increase
	 * @throws Iec104Exception iec exception
	 */
	protected void validateLen(int increase) throws Iec104Exception {
		if ((this.datas.size() * 3 + this.addresses.size() * 4 + increase) > 240) {
			throw new Iec104Exception("长度超长，不能再向此对象中添加元素");
		}
	}

	@Override
	public byte[][] handleAndAnswer(Apdu apdu) throws Exception {


		return null;
	}


	@Override
	public String toString() {
		String s = "无质量归一化值";
		if (addresses.size() == 1) {
			s += "连续寻址\n";
			s += addresses.get(0).toString() + "\n";
			for (Integer e : datas) {
				s += "值为 ：" + e + "\n";
			}
		} else {
			s += "单一寻址\n";
			int f = 0;
			for (Integer i : datas) {
				s += addresses.get(f++).toString();
				s += i.toString() + "\n";
			}
		}
		return s;
	}
}
