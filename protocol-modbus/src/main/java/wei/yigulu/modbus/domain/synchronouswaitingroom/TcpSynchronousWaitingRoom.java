package wei.yigulu.modbus.domain.synchronouswaitingroom;


import lombok.extern.slf4j.Slf4j;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;

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
		ByteBuffer byteBuffer = null;
		try {
			byteBuffer = guest.getData();
		} catch (InterruptedException e) {
			log.trace("响应超时，事务识别码为:" + key);
		}
		this.guestMap.remove(key);
		return byteBuffer;
	}

	@Override
	public void setData(ByteBuffer bytes) {
		if (bytes.remaining() > 2) {
			bytes.mark();
			int key = new P_AB().decode(bytes).getValue().intValue();
			bytes.reset();
			if (this.guestMap.containsKey(key)) {
				this.guestMap.get(key).setData(bytes);
			} else {
				log.trace("置入响应数据时，未发现等待者:" + key);
			}
		}
	}

	public class Guest {

		protected ByteBuffer bytes = null;

		public synchronized ByteBuffer getData() throws InterruptedException {
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
