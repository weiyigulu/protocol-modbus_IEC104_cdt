package wei.yigulu.modbus.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import wei.yigulu.modbus.domain.datatype.numeric.P_BA;
import wei.yigulu.netty.AbstractDelimiterHandler;
import wei.yigulu.utils.CrcUtils;
import wei.yigulu.utils.DataConvertor;


/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public class ModbusRtuMasterDelimiterHandler extends AbstractDelimiterHandler {


	/**
	 * 判断是否是断包的最大时间间隔
	 */
	@Setter
	@Getter
	@Accessors(chain = true)
	protected int maxTimeSpace = 1000;
	@Setter
	@Accessors(chain = true)
	/**
	 * 是否进行CRC校验判断
	 */
	private boolean doCrcCheck = true;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("接收到原始的报文 ：" + DataConvertor.ByteBuf2String((ByteBuf) msg));
		if (isOverMaxLength((ByteBuf) msg)) {
			return;
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
			cumulation.readBytes(1).release();
			functionCode = cumulation.readUnsignedByte();

			if (functionCode <= 0x04 || functionCode > 0x80) {
				if (functionCode > 0x80) {
					//异常功能码 异常帧
					byteNum = 0;
				} else {
					byteNum = cumulation.readUnsignedByte();
				}
				if (byteNum < 0 || byteNum > 250) {
					cumulation.resetReaderIndex();
					log.error("该帧字节长度不在规定范围内，整帧舍弃：" + DataConvertor.ByteBuf2String(cumulation.readBytes(cumulation.readableBytes())));
					clearCumulation();
					return;
				}
				if (this.doCrcCheck) {
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
						cumulation.resetReaderIndex();
						log.warn("数据帧crc校验错误，舍弃：" + DataConvertor.ByteBuf2String(cumulation) + "原CRC:" + DataConvertor.Byte2String(P_BA.decode(crcO)) + "理论CRC:" + DataConvertor.Byte2String(P_BA.decode(crcS)));
						clearCumulation();
						return;
					}
				} else {
					cumulation.resetReaderIndex();
					ctx.fireChannelRead(cumulation.readBytes(byteNum + 5));
				}
			} else if (functionCode == 05 || functionCode == 06 || functionCode == 15 || functionCode == 16) {
				if (cumulation.readableBytes() < 6) {
					break;
				}
				if (this.doCrcCheck) {
					bs = new byte[6];
					cumulation.resetReaderIndex();
					cumulation.readBytes(bs);
					crcO = cumulation.readUnsignedShortLE();
					crcS = CrcUtils.generateCRC16(bs).intValue();
					if (crcO == crcS) {
						cumulation.resetReaderIndex();
						ctx.fireChannelRead(cumulation.readBytes(8));
					} else {
						cumulation.resetReaderIndex();
						log.warn("数据帧crc校验错误，舍弃：" + DataConvertor.ByteBuf2String(cumulation) + "原CRC:" + DataConvertor.Byte2String(P_BA.decode(crcO)) + "理论CRC:" + DataConvertor.Byte2String(P_BA.decode(crcS)));
						clearCumulation();
						return;
					}
				} else {
					cumulation.resetReaderIndex();
					ctx.fireChannelRead(cumulation.readBytes(8));
				}
			} else {
				log.error("不支持该种类型报文解析");
				clearCumulation();
			}
		}
		if (cumulation.readableBytes() == 0) {
			clearCumulation();
		} else {
			timeMark = DateTime.now();
		}

	}


}
