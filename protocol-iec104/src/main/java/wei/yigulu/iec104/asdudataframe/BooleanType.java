package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.typemodel.IeBoolean;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;
import wei.yigulu.iec104.util.SendDataFrameHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 单点信息的数据类型帧
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class BooleanType extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.SINGEL_POINT_TYPE;


	private List<InformationBodyAddress> addresses = new ArrayList<>();

	private List<IeBoolean> datas = new ArrayList<>();

	/**
	 * Boolean type
	 *
	 * @param addresses addresses
	 * @param datas     datas
	 * @throws Iec104Exception iec exception
	 */
	public BooleanType(List<InformationBodyAddress> addresses, List<IeBoolean> datas) throws Iec104Exception {
		if (datas.size() > SendDataFrameHelper.MAXCONTINUITYYXNUM) {
			throw new Iec104Exception("数据个数过多，创建对象失败，请切割数据。");
		}
		if ((this.datas.size() * IeBoolean.OCCUPYBYTES + this.addresses.size() * InformationBodyAddress.OCCUPYBYTES) > 240) {
			throw new Iec104Exception("长度超长，创建对象失败，请切割数据。");
		}
		this.addresses = addresses;
		this.datas = datas;
	}


	/**
	 * 向datas中添加数据，默认的质量描述
	 *
	 * @param f f
	 * @throws Iec104Exception iec exception
	 */
	public void addData(boolean f) throws Iec104Exception {
		validateLen(IeBoolean.OCCUPYBYTES);
		this.datas.add(new IeBoolean(f));
	}


	/**
	 * 向datas中添加数据和数据地址
	 *
	 * @param address address
	 * @param f       f
	 * @throws Iec104Exception iec exception
	 */
	public void addDataAndAdd(InformationBodyAddress address, boolean f) throws Iec104Exception {
		addAddress(address);
		addData(f);
	}


	/**
	 * 向datas中添加数据和数据地址
	 *
	 * @param address address
	 * @throws Iec104Exception iec exception
	 */
	public void addAddress(InformationBodyAddress address) throws Iec104Exception {
		validateLen(InformationBodyAddress.OCCUPYBYTES);
		this.addresses.add(address);
	}

	/**
	 * Validate len *
	 *
	 * @param increase increase
	 * @throws Iec104Exception iec exception
	 */
	protected void validateLen(int increase) throws Iec104Exception {
		if (datas.size() > SendDataFrameHelper.MAXCONTINUITYYXNUM) {
			throw new Iec104Exception("数据个数过多，不能再向此对象中添加元素");
		}
		if ((this.datas.size() * IeBoolean.OCCUPYBYTES + this.addresses.size() * InformationBodyAddress.OCCUPYBYTES + increase) > 240) {
			throw new Iec104Exception("长度超长，不能再向此对象中添加元素");
		}
	}

	@Override
	public void encode(List<Byte> buffer) {
		if (addresses.size() == 1) {
			addresses.get(0).encode(buffer);
			for (IeBoolean i : datas) {
				buffer.add((byte) i.encode());
			}
		} else {
			for (int i = 0; i < datas.size(); i++) {
				addresses.get(i).encode(buffer);
				buffer.add((byte) (datas.get(i).encode()));
			}
		}
	}

	@Override
	public Asdu generateBack() {
		Asdu asdu = new Asdu();
		asdu.setTypeId(1);
		asdu.setDataFrame(this);
		asdu.getVsq().setSq(this.addresses.size() == 1 ? 1 : 0);
		asdu.getVsq().setNum(this.datas.size());
		asdu.setOriginatorAddress(0);
		asdu.setCommonAddress(1);
		return asdu;
	}

	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {
		try {
			if (vsq.getSq() == 0) {
				for (int i = 0; i < vsq.getNum(); i++) {
					addresses.add(new InformationBodyAddress(is));
					datas.add(new IeBoolean(is));
				}
			} else {
				addresses.add(new InformationBodyAddress(is));
				for (int i = 0; i < vsq.getNum(); i++) {
					datas.add(new IeBoolean(is));
				}
			}
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
		StringBuilder s = new StringBuilder("单点信息\n");
		if (addresses.size() == 1) {
			s.append(addresses.get(0).toString()).append("\n");
			for (IeBoolean i : datas) {
				s.append(i.toString());
			}
		} else {
			for (int i = 0; i < datas.size(); i++) {
				s.append(addresses.get(i).toString());
				s.append(datas.get(i).toString());
			}
		}
		return s.toString();
	}

}
