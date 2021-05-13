package wei.yigulu.iec104.util;

import lombok.extern.slf4j.Slf4j;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.asdudataframe.ShortFloatCommand;
import wei.yigulu.iec104.asdudataframe.typemodel.IecDataInterface;
import wei.yigulu.netty.AbstractMasterBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 发送控制命令的工具类
 *
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class SendCommandHelper {

	private static List<CommandWaiter> commandWaiters = Collections.synchronizedList(new ArrayList());


	public static boolean sendShortCommand(AbstractMasterBuilder masterBuilder, Integer sourceAddress, Integer commonAddress, Integer dataAddress, Float value) throws Exception {
		ShortFloatCommand command = new ShortFloatCommand(dataAddress, value);
		Apdu apdu = new Apdu();
		Asdu asdu = command.generateBack();
		asdu.setCommonAddress(commonAddress);
		asdu.setOriginatorAddress(sourceAddress);
		asdu.getCot().setNot(6);
		apdu.setAsdu(asdu);
		SendAndReceiveNumUtil.sendIFrame(apdu, masterBuilder.getFuture().channel(), masterBuilder.getLog());
		CommandWaiter commandWaiter = new CommandWaiter(masterBuilder.getFuture().channel().id(), apdu, 0);
		commandWaiters.add(commandWaiter);
		IecDataInterface data = commandWaiter.get();
		if (value.equals(data.getIecValue())) {
			return true;
		} else {
			return false;
		}
	}


	public static void setIecValue(CommandWaiter commandWaiter) {
		int i = commandWaiters.indexOf(commandWaiter);
		System.out.println(i);
		if (i != -1) {
			commandWaiters.get(i).set(commandWaiter.getData());
		} else {

		}
	}
}

