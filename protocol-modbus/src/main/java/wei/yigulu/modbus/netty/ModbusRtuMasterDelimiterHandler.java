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
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusRtuMasterDelimiterHandler extends ChannelInboundHandlerAdapter {

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

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//log.warn("接收到原始的报文 ："+ DataConvertor.ByteBuf2String((ByteBuf) msg));
		if (cumulation == null) {
			cumulation = (ByteBuf) msg;
			if (cumulation.readableBytes() > maxLength) {
				while (!cumulation.release()) {
				}
				cumulation = null;
				log.warn("报文超长舍弃");
				return;
			}
		} else {
			if (timeMark.plusMillis(50).isBeforeNow()) {
				log.warn("上一帧数据长度不足，但两帧时间间隔较长上一帧被舍弃 舍弃的数据帧为：" + DataConvertor.ByteBuf2String(cumulation));
				while (!cumulation.release()) {
				}
				cumulation = (ByteBuf) msg;
			} else {
				//拓展寄居buffer
				cumulation = expandCumulation(cumulation, (ByteBuf) msg);
			}
		}
		//原crc值
		int crcO;
		//理论crc值
		int crcS;
		byte[] bs;
		int byteNum;
		int functionCode;
		while (cumulation.readableBytes() >= 5) {
			cumulation.markReaderIndex();
			cumulation.readBytes(1);
			functionCode = cumulation.readUnsignedByte();
			if (functionCode > 0x80) {
				//异常功能码 异常帧
				byteNum = 0;
			} else {
				byteNum = cumulation.readByte();
			}
			if (byteNum < 0 || byteNum > 250) {
				cumulation.resetReaderIndex();
				log.error("该帧字节长度不在规定范围内，整帧舍弃：" + DataConvertor.ByteBuf2String(cumulation.readBytes(cumulation.readableBytes())));
				while (!cumulation.release()) {
				}
				cumulation = null;
			}
			bs = new byte[byteNum + 3];
			cumulation.resetReaderIndex();
			if (cumulation.readableBytes() < byteNum + 5) {
				break;
			}
			cumulation.readBytes(bs);
			crcO = cumulation.readUnsignedShortLE();
			crcS = CrcUtils.generateCRC16(bs).intValue();
			if (crcO == crcS) {
				cumulation.resetReaderIndex();
				ctx.fireChannelRead(cumulation.readBytes(byteNum + 5));
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
