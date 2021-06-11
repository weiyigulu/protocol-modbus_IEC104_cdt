package wei.yigulu.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wei.yigulu.utils.DataConvertor;

/**
 * 未继承netty的数据帧处理拆包类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

public abstract class AbstractDelimiterHandler extends ChannelInboundHandlerAdapter {

	@Setter
	@Accessors(chain = true)
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 寄居bytebuf  用于拆包缓冲
	 */
	protected ByteBuf cumulation;

	/**
	 * 时间标记 记录上一帧发生时间
	 */
	protected DateTime timeMark = DateTime.now();


	/**
	 * 接收的最长的报文长度
	 */
	@Setter
	@Getter
	@Accessors(chain = true)
	protected int maxLength = 10240;


	/**
	 * 判断是否是断包的最大时间间隔
	 */
	@Setter
	@Getter
	@Accessors(chain = true)
	protected int maxTimeSpace = 200;


	/**
	 * 拓展寄居 ByteBuf
	 * 拓展规则是: 初始容量为 两个ByteBuf的长度和,内容是byteBuf1未读部分+ byteBuf2未读部分。
	 *
	 * @param byteBuf1
	 * @param byteBuf2
	 * @return {@link ByteBuf}
	 */
	protected static ByteBuf expandCumulation(ByteBuf byteBuf1, ByteBuf byteBuf2) {
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
	public abstract void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;


	/**
	 * 获取ByteBuf 中某一段byte数组的位置 ByteBuf中游标位置不变
	 * 获得帧头的位置
	 *
	 * @param from    开始游标
	 * @param end     结束游标
	 * @param byteBuf 被检索的ByteBuf
	 * @param head    头字节数组
	 * @return int  头位置
	 */
	protected int getHeadIndex(int from, int end, ByteBuf byteBuf, byte[] head) {
		if (byteBuf.readableBytes() < head.length) {
			return -1;
		}
		for (int i = from; i < end; i++) {
			if (isEqualByteArr(head, ByteBufUtil.getBytes(byteBuf, i, head.length))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 清除寄存ByteBuf的指向和内容
	 */
	protected void clearCumulation() {
		while (!cumulation.release()) {
		}
		cumulation = null;
	}

	/**
	 * 清除寄存ByteBuf的指向  并提供新的指向内容重新赋值
	 */
	protected void setCumulation(ByteBuf byteBuf) {
		while (!cumulation.release()) {
		}
		cumulation = byteBuf;
	}

	/**
	 * 根据时间跨度判断数据帧是合并还是舍弃
	 *
	 * @param byteBuf 字节缓冲区
	 */
	protected void mergeOrFlushByTimeSpan(ByteBuf byteBuf) {
		if (timeMark.plusMillis(getMaxTimeSpace()).isBeforeNow()) {
			log.warn("上一帧数据长度不足，但两帧时间间隔较长上一帧被舍弃 舍弃的数据帧为：" + DataConvertor.ByteBuf2String(cumulation));
			while (!cumulation.release()) {
			}
			cumulation = byteBuf;
		} else {
			//拓展寄居buffer
			cumulation = expandCumulation(cumulation, byteBuf);
		}
	}

	/**
	 * 判断写入报文是否超过最大长度
	 * 如果超过则清除缓存区内容
	 * 否则进行合并
	 *
	 * @param byteBuf 字节缓冲区
	 * @return boolean
	 */
	protected boolean isOverMaxLength(ByteBuf byteBuf) {
		if (byteBuf.readableBytes() > getMaxLength()) {
			while (!cumulation.release()) {
			}
			cumulation = null;
			log.warn("报文超长舍弃");
			return true;
		} else {
			if (cumulation == null) {
				cumulation = byteBuf;
			} else {
				mergeOrFlushByTimeSpan(byteBuf);
			}
			return false;
		}

	}

	/**
	 * 判断两个字节数据是否相等
	 *
	 * @param b1 b1
	 * @param b2 b2
	 * @return boolean
	 */
	protected boolean isEqualByteArr(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			return false;
		}
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) {
				return false;
			}
		}
		return true;
	}

}
