package wei.yigulu.modbus.domain.tcpextracode;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusMasterBuilderInterface;
import wei.yigulu.netty.AbstractMasterBuilder;
import wei.yigulu.netty.AbstractTcpMasterBuilder;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务标识符
 *
 * @author xiuwei
 */
@Accessors(chain = true)
public class TransactionIdentifier {


	static Map<AbstractMasterBuilder, Integer> counters = new ConcurrentHashMap<>();


	/**
	 * 发送报文的顺序号
	 */
	@Setter
	@Getter
	private int seq;

	/**
	 * 通讯时的附加码1 事务标识符1
	 */
	@Setter
	private Byte extraCode1 = 0;
	/**
	 * 通讯时的附加码2 事务标识符2
	 */
	@Setter
	private Byte extraCode2 = 0;

	public static TransactionIdentifier getInstance(AbstractTcpMasterBuilder masterBuilder) throws ModbusException {
		if (!((masterBuilder instanceof ModbusMasterBuilderInterface) && (masterBuilder instanceof AbstractTcpMasterBuilder))) {
			throw new ModbusException("请传人实现了<ModbusMasterBuilderInterface>的TCPMaster");
		}
		Integer i = 0;
		if (counters.containsKey(masterBuilder)) {
			i = counters.get(masterBuilder);
			if (i == 65535) {
				i = 0;
			} else {
				i++;
			}
		}
		counters.put(masterBuilder, i);
		return new TransactionIdentifier().setSeq(i).setExtraCode1((byte) (i >> 8)).setExtraCode2((byte) (i & 0xff));
	}

	public void encode(List<Byte> bytes) {
		bytes.add(extraCode1);
		bytes.add(extraCode2);
	}

	public void decode(ByteBuffer byteBuf) {
		extraCode1 = byteBuf.get();
		extraCode2 = byteBuf.get();
		this.setSeq(((extraCode1 & 0xff) << 8) | (extraCode2 & 0xff));
	}
}
