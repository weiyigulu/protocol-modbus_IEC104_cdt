package wei.yigulu.iec104.util;

import io.netty.channel.ChannelId;
import lombok.Data;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.asdudataframe.typemodel.IecDataInterface;

/**
 * 命令等待者
 *
 * @author xiuwei
 * @date 2021/05/11
 */
@Data
public class CommandWaiter {

	/**
	 * 通道Id
	 */
	ChannelId channelId;
	/**
	 * 源地址
	 */
	Integer sourceAddress;
	/**
	 * 公共地址
	 */
	Integer commonAddress;
	/**
	 * 报文类型
	 */
	Integer typeId;
	/**
	 * 数据地址位   在设计上暂时未考虑136类型的多点同时控制的情况
	 */
	Integer dataAddress;

	IecDataInterface data = null;

	public CommandWaiter(ChannelId channelId, Apdu apdu, Integer dataAddress) {
		this.channelId = channelId;
		this.commonAddress = apdu.getAsdu().getCommonAddress();
		this.sourceAddress = apdu.getAsdu().getOriginatorAddress();
		this.commonAddress = apdu.getAsdu().getCommonAddress();
		this.typeId = apdu.getAsdu().getTypeId();
		this.dataAddress = dataAddress;
	}

	public synchronized IecDataInterface get() {
		if (this.data == null) {
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.data;
	}

	public synchronized void set(IecDataInterface data) {
		this.data = data;
		this.notifyAll();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CommandWaiter that = (CommandWaiter) o;

		if (!channelId.equals(that.channelId)) return false;
		if (!sourceAddress.equals(that.sourceAddress)) return false;
		if (!commonAddress.equals(that.commonAddress)) return false;
		if (!typeId.equals(that.typeId)) return false;
		return dataAddress.equals(that.dataAddress);
	}

	@Override
	public int hashCode() {
		int result = channelId.hashCode();
		result = 31 * result + sourceAddress.hashCode();
		result = 31 * result + commonAddress.hashCode();
		result = 31 * result + typeId.hashCode();
		result = 31 * result + dataAddress.hashCode();
		return result;
	}
}
