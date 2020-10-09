package wei.yigulu.modbus.domain.synchronouswaitingroom;

import java.nio.ByteBuffer;

/**
 * @program: modbus
 * @description: 同步等待室
 * @author: xiuwei
 * @create: 2020-08-06 16:37
 */
public interface SynchronousWaitingRoom {

	/**
	 * 获取数据
	 *
	 * @param key 钥匙
	 * @return {@link ByteBuffer}
	 */
	ByteBuffer getData(int key);


	/**
	 * 设置数据
	 *
	 * @param bytes 字节缓冲
	 */
	void setData(ByteBuffer bytes);
}
