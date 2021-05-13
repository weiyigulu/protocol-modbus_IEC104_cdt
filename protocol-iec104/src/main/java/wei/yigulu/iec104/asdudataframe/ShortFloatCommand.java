package wei.yigulu.iec104.asdudataframe;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.qualitydescription.IeMeasuredQuality;
import wei.yigulu.iec104.asdudataframe.typemodel.IeShortFloat;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;
import wei.yigulu.iec104.util.CommandWaiter;
import wei.yigulu.iec104.util.SendAndReceiveNumUtil;
import wei.yigulu.iec104.util.SendCommandHelper;

import java.util.List;

/**
 * 遥测控制命令
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
@Data
public class ShortFloatCommand extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.SHORT_FLOAT_COMMAND_TYPE;

	private InformationBodyAddress addresses = new InformationBodyAddress();

	private Float val = new Float(0);

	private IeMeasuredQuality quality = new IeMeasuredQuality();


	/**
	 * 短浮命令
	 *
	 * @param address 地址
	 * @param val     值
	 * @param quality 质量
	 */
	public ShortFloatCommand(Integer address, Float val, IeMeasuredQuality quality) {
		if (address != null) {
			this.addresses = new InformationBodyAddress(address);
		}
		if (val != null) {
			this.val = val;
		}
		if (quality != null) {
			this.quality = quality;
		}
	}

	/**
	 * 短浮命令
	 *
	 * @param address 地址
	 * @param val     值
	 */
	public ShortFloatCommand(Integer address, Float val) {
		new ShortFloatCommand(address, val, null);
	}


	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {
		try {
			this.addresses = new InformationBodyAddress(is);
			this.val = new IeShortFloat(is).getValue();
			this.quality = new IeMeasuredQuality(is);
		} catch (Iec104Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(List<Byte> buffer) {
		this.addresses.encode(buffer);
		new IeShortFloat(this.val).encode(buffer);
		buffer.add((byte) this.quality.encode());
	}

	@Override
	public Asdu generateBack() {
		Asdu asdu = new Asdu();
		asdu.setTypeId(TYPEID);
		asdu.setDataFrame(this);
		asdu.getVsq().setSq(0);
		asdu.getVsq().setNum(1);
		asdu.setOriginatorAddress(0);
		asdu.setCommonAddress(1);
		return asdu;
	}

	@Override
	public byte[][] handleAndAnswer(Apdu apdu) throws Exception {
		if (apdu.getAsdu().getCot().getNot() == 6) {
			byte[][] bs = new byte[1][];
			apdu.getAsdu().getCot().setNot(7);
			SendAndReceiveNumUtil.setSendAndReceiveNum(apdu, apdu.getChannel().id());
			bs[0] = apdu.encode();
			return bs;
		} else {
			ShortFloatCommand shortFloatCommand = (ShortFloatCommand) apdu.getAsdu().getDataFrame();
			CommandWaiter commandWaiter = new CommandWaiter(apdu.getChannel().id(), apdu, shortFloatCommand.getAddresses().getAddress());
			commandWaiter.set(new IeShortFloat(shortFloatCommand.getVal()));
			SendCommandHelper.setIecValue(commandWaiter);
			return null;
		}
	}


	@Override
	public String toString() {
		String s = "短浮点控制命令——";
		s += "地址：" + this.addresses.toString();
		s += "设定值：" + this.val + ";";
		s += this.quality.toString();
		return s;
	}
}
