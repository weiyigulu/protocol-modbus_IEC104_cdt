package wei.yigulu.iec104.util;


import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.container.Iec104Link;
import wei.yigulu.iec104.container.LinkContainer;
import wei.yigulu.utils.DataConvertor;


/**
 * 用以处理接收和发送序列号的处理类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public class SendAndReceiveNumUtil {

	private static Logger log = LoggerFactory.getLogger(SendAndReceiveNumUtil.class);

	private static final String INTERVALPROPNAME = "sFrameInterval";

	private static final int INTERVALDEFVAL = 5;

	private static final int INTERVAL = PropertiesReader.getInstance().getIntProp(INTERVALPROPNAME, INTERVALDEFVAL);


	/**
	 * 为发送的i帧组装接收和发送序号
	 *
	 * @param apdu      apdu
	 * @param channelId channel id
	 */
	public static void setSendAndReceiveNum(Apdu apdu, ChannelId channelId) {
		Iec104Link link = LinkContainer.getInstance().getLink(channelId);
		int send = link.getISend();
		int receive = link.getIReceive();
		apdu.setSendSeqNum(send++);
		apdu.setReceiveSeqNum(receive);
		link.setISend(send);
		LinkContainer.getInstance().getLinks().put(channelId, link);
	}


	/**
	 * 组装i帧的发送和接收序号 后发出
	 *
	 * @param apdu    apdu
	 * @param channel channel
	 * @throws Exception exception
	 */
	public static void sendIFrame(Apdu apdu, Channel channel) throws Exception {
		setSendAndReceiveNum(apdu, channel.id());
		byte[] bb = apdu.encode();
		log.debug("向104对端发出数据帧：" + DataConvertor.Byte2String(bb));
		channel.writeAndFlush(Unpooled.copiedBuffer(bb));
	}


	/**
	 * 组装i帧的发送和接收序号 后发出
	 *
	 * @param apdu    apdu
	 * @param channel channel
	 * @throws Exception exception
	 */
	public static void sendIFrame(Apdu apdu, Channel channel, Logger log) throws Exception {
		setSendAndReceiveNum(apdu, channel.id());
		byte[] bb = apdu.encode();
		if (log != null) {
			log.debug("向104对端发出数据帧：" + DataConvertor.Byte2String(bb));
		} else {
			SendAndReceiveNumUtil.log.debug("向104对端发出数据帧：" + DataConvertor.Byte2String(bb));
		}
		channel.writeAndFlush(Unpooled.copiedBuffer(bb));
	}

	/**
	 * 接收到i帧，处理接收和发送序号
	 *
	 * @param apdu      apdu
	 * @param channelId channel id
	 */
	public static void receiveIFrame(Apdu apdu, ChannelId channelId) {
		Iec104Link link = LinkContainer.getInstance().getLink(channelId);
		int send = link.getISend();
		int receive = link.getIReceive();
		int send1 = apdu.getSendSeqNum();
		int receive1 = apdu.getReceiveSeqNum();
		link.setLinkState(Iec104Link.LinkState.NORMAL);
		if (receive < send1) {
			/**
			 * 我方丢失 通道对方放出的 i帧
			 */
			apdu.loseReceive();
			link.setLinkState(Iec104Link.LinkState.LOSEREC);
		}
		if (send < receive1) {
			/**
			 * 通道对方丢失 我方发出的i帧
			 */
			apdu.loseSend();
			link.setLinkState(Iec104Link.LinkState.LOSESEND);
		}
		link.setIReceive(++send1);
		link.setISend(receive1);
		sendSFrame(link);
		LinkContainer.getInstance().getLinks().put(channelId, link);
	}

	/**
	 * 向通道内发送s帧
	 *
	 * @param link link
	 */
	public static void sendSFrame(Iec104Link link) {

		if (link.getIReceive() != 0 && link.getIReceive() % INTERVAL == 0) {
			Apdu apdu1 = new Apdu();
			apdu1.setReceiveSeqNum(link.getIReceive());
			apdu1.setApciType(Apdu.ApciType.S_FORMAT);
			try {
				byte[] bs = apdu1.encode();
				//TODO  改变日志模式
				link.getLog().debug("发送s帧：" + DataConvertor.Byte2String(bs));
				link.getChannel().writeAndFlush(Unpooled.copiedBuffer(bs));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
