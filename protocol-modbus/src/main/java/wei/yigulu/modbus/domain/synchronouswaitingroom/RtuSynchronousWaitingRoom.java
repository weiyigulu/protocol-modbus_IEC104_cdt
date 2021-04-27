package wei.yigulu.modbus.domain.synchronouswaitingroom;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 同步等待室 将请求和响应同步起来
 *
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class RtuSynchronousWaitingRoom implements SynchronousWaitingRoom {

	public static long waitTime = 2000L;

	protected ByteBuffer bytes = null;

	protected boolean hasGuest;


	@Override
	@SneakyThrows
	public ByteBuffer getData(int key) {
		ByteBuffer returnBytes = null;
		hasGuest = true;
		synchronized (this) {
			try {
				if (this.bytes == null) {
					this.wait(RtuSynchronousWaitingRoom.waitTime);
				}
				if (this.bytes != null && this.bytes.remaining() != 0) {
					returnBytes = this.bytes;
				} else {
					log.warn("响应超时");
				}
			} catch (Exception e) {
				hasGuest = false;
				this.bytes = null;
				throw e;
			}
		}
		this.bytes = null;
		hasGuest = false;
		return returnBytes;
	}


	@Override
	public void setData(ByteBuffer bytes) {
		if (this.hasGuest) {
			synchronized (this) {
				this.bytes = bytes;
				this.notify();
			}
		}
	}

}
