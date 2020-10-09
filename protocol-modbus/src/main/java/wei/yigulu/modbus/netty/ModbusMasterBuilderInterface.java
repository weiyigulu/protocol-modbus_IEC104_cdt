package wei.yigulu.modbus.netty;

import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;

/**
 * modbus  master的构建器
 *
 * @author: xiuwei
 * @version:
 */
public interface ModbusMasterBuilderInterface {


	/**
	 * null则创建，有则获取获取EventLoopGroup 用与bootstrap的绑定
	 *
	 * @return or create work group
	 */
	SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() throws ModbusException;

}
