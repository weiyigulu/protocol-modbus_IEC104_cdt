package wei.yigulu.iec104.asdudataframe;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;
import wei.yigulu.iec104.asdudataframe.qualitydescription.IeMeasuredQuality;
import wei.yigulu.iec104.asdudataframe.typemodel.IeShortFloat;
import wei.yigulu.iec104.asdudataframe.typemodel.IeSingleBooleanCommand;
import wei.yigulu.iec104.asdudataframe.typemodel.InformationBodyAddress;
import wei.yigulu.iec104.exception.Iec104Exception;
import wei.yigulu.iec104.nettyconfig.TechnicalTerm;
import wei.yigulu.iec104.util.CommandWaiter;
import wei.yigulu.iec104.util.SendAndReceiveNumUtil;
import wei.yigulu.iec104.util.SendCommandHelper;

import java.util.List;

/**
 * 双点遥控命令
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
@Data
public class SingleBooleanCommand extends AbstractDataFrameType {

	/**
	 * TYPEID
	 */
	public static final int TYPEID = TechnicalTerm.SINGLE_BOOLEAN_COMMAND_TYPE;

	private InformationBodyAddress addresses = new InformationBodyAddress();

	private IeSingleBooleanCommand val = new IeSingleBooleanCommand(false);


	/**
	 * 短浮命令
	 *
	 * @param address 地址
	 * @param val     值
	 */
	public SingleBooleanCommand(Integer address, boolean val) {
		if (address != null) {
			this.addresses = new InformationBodyAddress(address);
		}
		this.val = new IeSingleBooleanCommand(val);
	}


	@Override
	public void loadByteBuf(ByteBuf is, Vsq vsq) {
		try {
			this.addresses = new InformationBodyAddress(is);
			this.val = new IeSingleBooleanCommand(is);
		} catch (Iec104Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(List<Byte> buffer) {
		this.addresses.encode(buffer);
		this.val.encode(buffer);
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
			SingleBooleanCommand singleBooleanCommand = (SingleBooleanCommand) apdu.getAsdu().getDataFrame();
			CommandWaiter commandWaiter = new CommandWaiter(apdu.getChannel().id(), apdu, singleBooleanCommand.getAddresses().getAddress());
			commandWaiter.set(singleBooleanCommand.getVal());
			SendCommandHelper.setIecValue(commandWaiter);
			return null;
		}
	}


	@Override
	public String toString() {
		String s = "单点遥控命令——";
		s += this.addresses.toString();
		s += this.val;
		return s;
	}
}
