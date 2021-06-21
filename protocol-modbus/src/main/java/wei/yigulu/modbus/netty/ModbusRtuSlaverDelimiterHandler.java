package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import wei.yigulu.modbus.domain.datatype.numeric.P_BA;
import wei.yigulu.netty.AbstractDelimiterHandler;
import wei.yigulu.utils.CrcUtils;
import wei.yigulu.utils.DataConvertor;


/**
 * modbus RTU slave的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusRtuSlaverDelimiterHandler extends AbstractDelimiterHandler {

	/**
	 * 单个请求帧的的长度
	 */
	private static final int SingleLength = 8;


	/**
	 * slaver 只接受八字节的数据请求帧 [slaveID][functionCode][startAddress1][startAddress2][dataNum1][dataNum2][crc1][crc2]]
	 *
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		log.warn("接收到原始的报文 ："+ DataConvertor.ByteBuf2String((ByteBuf) msg));
		isOverMaxLength((ByteBuf) msg);
		if (cumulation.readableBytes() < 8) {
			timeMark = DateTime.now();
			return;
		}
		byte function = cumulation.getByte(1);
		byte[] bs;
		if (function == 5 || function == 6) {
			bs = new byte[6];
		} else if (function == 15 || function == 16) {
			int i = cumulation.getByte(6);
			if (i >= 0 && i < 250) {
				bs = new byte[7 + i];
			} else {
				clearCumulation();
				return;
			}
		} else {
			bs = new byte[6];
		}
		//原crc值
		int crcO;
		//理论crc值
		int crcS;
		while (cumulation.readableBytes() >= SingleLength) {
			cumulation.markReaderIndex();
			cumulation.readBytes(bs);
			crcO = cumulation.readUnsignedShortLE();
			crcS = CrcUtils.generateCRC16(bs).intValue();
			if (crcO == crcS) {
				cumulation.resetReaderIndex();
				ctx.fireChannelRead(cumulation.readBytes(bs.length+2));
			} else {
				log.warn("数据帧crc校验错误，舍弃：" + DataConvertor.Byte2String(bs) + "原CRC:" + DataConvertor.Byte2String(P_BA.decode(crcO)) + "理论CRC:" + DataConvertor.Byte2String(P_BA.decode(crcS)));
			}
		}
		if (cumulation.readableBytes() == 0) {
			clearCumulation();
		} else {
			timeMark = DateTime.now();
		}
	}


}
