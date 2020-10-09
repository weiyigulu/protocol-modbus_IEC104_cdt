package wei.yigulu.modbus.domain.synchronouswaitingroom;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同步等待室 将请求和响应同步起来
 *
 * @author: xiuwei
 * @version:
 */
@Slf4j
public class TcpSynchronousWaitingRoom implements SynchronousWaitingRoom {


	public static long waitTime = 2000L;


	protected Map<Integer, Guest> guestMap = new ConcurrentHashMap<>();


	@Override
	public ByteBuffer getData(int key) {
		Guest guest = new Guest();
		this.guestMap.put(key, guest);
		ByteBuffer byteBuffer = guest.getData();
		this.guestMap.remove(key);
		return byteBuffer;
	}

	@Override
	public void setData(ByteBuffer bytes) {
		if (bytes.remaining() > 2) {
			bytes.mark();
			int key = bytes.getShort();
			bytes.reset();
			if (this.guestMap.containsKey(key)) {
				this.guestMap.get(key).setData(bytes);
			}
		}
	}

	public class Guest {

		protected ByteBuffer bytes = null;

		@SneakyThrows
		public synchronized ByteBuffer getData() {
			ByteBuffer returnBytes = null;
			try {
				if (this.bytes == null) {
					this.wait(TcpSynchronousWaitingRoom.waitTime);
				}
				if (this.bytes != null && this.bytes.remaining() != 0) {
					returnBytes = this.bytes;
				} else {
					log.warn("响应超时");
				}
			} catch (Exception e) {
				this.bytes = null;
				throw e;
			}
			this.bytes = null;
			return returnBytes;
		}

		public synchronized void setData(ByteBuffer bytes) {
			this.bytes = bytes;
			this.notifyAll();
		}
	}

}
