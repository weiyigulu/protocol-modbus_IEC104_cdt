package wei.yigulu.modbus.netty;

import wei.yigulu.modbus.domain.synchronouswaitingroom.SynchronousWaitingRoom;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.netty.MasterInterface;

/**
 * modbus  master的构建器
 *
 * @author: xiuwei
 * @version:
 */
public interface ModbusMasterBuilderInterface extends MasterInterface {


	/**
	 * 获取或创建同步等候室
	 * null则创建，有则获取获取EventLoopGroup 用与bootstrap的绑定
	 *
	 * @return or create work group
	 * @throws ModbusException modbus例外
	 */
	SynchronousWaitingRoom getOrCreateSynchronousWaitingRoom() throws ModbusException;

}
