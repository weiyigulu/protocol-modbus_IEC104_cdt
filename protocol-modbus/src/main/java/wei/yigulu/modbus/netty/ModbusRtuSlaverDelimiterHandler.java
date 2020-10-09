package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.modbus.domain.datatype.numeric.P_BA;
import wei.yigulu.utils.CrcUtils;
import wei.yigulu.utils.DataConvertor;


/**
 * modbus RTU slave的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusRtuSlaverDelimiterHandler extends ChannelInboundHandlerAdapter {

	/**
	 * 单个请求帧的的长度
	 */
	private static final int SingleLength = 8;
	public static int maxLength = 256;
	@Setter
	@Accessors(chain = true)
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ByteBuf cumulation;

	private DateTime timeMark = DateTime.now();

	private static ByteBuf expandCumulation(ByteBuf byteBuf1, ByteBuf byteBuf2) {
		ByteBuf oldCumulation = byteBuf1;
		byteBuf1 = byteBuf1.alloc().buffer(oldCumulation.readableBytes() + byteBuf2.readableBytes());
		byteBuf1.writeBytes(oldCumulation);
		byteBuf1.writeBytes(byteBuf2);
		byteBuf1.readerIndex(0);
		while (!oldCumulation.release()) {
		}
		while (!byteBuf2.release()) {
		}
		return byteBuf1;
	}

	/**
	 * slaver 只接受八字节的数据请求帧 [slaveID][functionCode][startAddress1][startAddress2][dataNum1][dataNum2][crc1][crc2]]
	 *
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		//log.warn("接收到原始的报文 ："+ DataConvertor.ByteBuf2String((ByteBuf) msg));
		if (cumulation == null) {
			cumulation = (ByteBuf) msg;

		} else {
			if (timeMark.plusMillis(50).isBeforeNow()) {
				log.warn("上一帧数据长度不足，但两帧时间间隔较长上一帧被舍弃 舍弃的数据帧为：" + DataConvertor.ByteBuf2String(cumulation));
				while (!cumulation.release()) {
				}
				cumulation = (ByteBuf) msg;
			} else if (((ByteBuf) msg).readableBytes() % SingleLength == 0) {
				//如果过来的数据帧长度是8的整数倍  证明该帧可能由n个请求帧构成
				while (!cumulation.release()) {
				}
				cumulation = (ByteBuf) msg;
			} else {
				cumulation = expandCumulation(cumulation, (ByteBuf) msg);
			}
		}

		byte[] bs = new byte[6];
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
				ctx.fireChannelRead(cumulation.readBytes(8));
			} else {
				log.warn("数据帧crc校验错误，舍弃：" + DataConvertor.Byte2String(bs) + "原CRC:" + DataConvertor.Byte2String(P_BA.decode(crcO)) + "理论CRC:" + DataConvertor.Byte2String(P_BA.decode(crcS)));
			}
		}
		if (cumulation.readableBytes() > 0) {
			ByteBuf buf = cumulation.readBytes(cumulation.readableBytes());
			while (!cumulation.release()) {
			}
			cumulation = buf;
			timeMark = DateTime.now();
		} else {
			while (!cumulation.release()) {
			}
			cumulation = null;
		}
	}


}
