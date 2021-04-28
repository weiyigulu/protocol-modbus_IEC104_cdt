package wei.yigulu.modbus.utils;

import com.google.common.primitives.Bytes;
import io.netty.buffer.Unpooled;
import wei.yigulu.modbus.domain.command.RtuModbusCommand;
import wei.yigulu.modbus.domain.command.TcpModbusCommand;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.request.TcpModbusRequest;
import wei.yigulu.modbus.domain.tcpextracode.TransactionIdentifier;
import wei.yigulu.modbus.exceptiom.ModbusException;
import wei.yigulu.modbus.netty.ModbusMasterBuilderInterface;
import wei.yigulu.modbus.netty.ModbusRtuMasterBuilder;
import wei.yigulu.netty.AbstractMasterBuilder;
import wei.yigulu.netty.AbstractTcpMasterBuilder;
import wei.yigulu.utils.DataConvertor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * modbs下达数据命令的工具类
 *
 * @author: xiuwei
 * @version:
 */
public class ModbusCommandDataUtils {


	/**
	 * 下达寄存器控制命令
	 *
	 * @param masterBuilder 主站对象
	 * @param address       起始地址
	 * @param values        命令值
	 */
	public static void commandRegister(AbstractMasterBuilder masterBuilder,Integer slaveId,Integer address, List<RegisterValue> values) throws ModbusException {
		if (!(masterBuilder instanceof ModbusMasterBuilderInterface)) {
			throw new RuntimeException("请传人实现了<ModbusMasterBuilderInterface>的Master");
		}
		List<Byte> bs=new ArrayList<>();
		ByteBuffer buffer;
		if(masterBuilder instanceof ModbusRtuMasterBuilder){
			RtuModbusCommand rtuModbusCommand =new RtuModbusCommand();
			rtuModbusCommand.setSlaveId(slaveId).setRegisters(address,values);
			rtuModbusCommand.encode(bs);
			masterBuilder.sendFrameToOpposite(Bytes.toArray(bs));
			buffer = ((ModbusMasterBuilderInterface) masterBuilder).getOrCreateSynchronousWaitingRoom().getData(0);
		}else{
			TcpModbusCommand tcpModbusCommand =new TcpModbusCommand();
			tcpModbusCommand.setTransactionIdentifier(TransactionIdentifier.getInstance((AbstractTcpMasterBuilder) masterBuilder));
			tcpModbusCommand.setSlaveId(slaveId).setRegisters(address,values);
			tcpModbusCommand.encode(bs);
			masterBuilder.sendFrameToOpposite(Bytes.toArray(bs));
			buffer = ((ModbusMasterBuilderInterface) masterBuilder).getOrCreateSynchronousWaitingRoom().getData(tcpModbusCommand.getTcpExtraCode().getTransactionIdentifier().getSeq());
		}
		System.out.println(DataConvertor.ByteBuf2String(Unpooled.copiedBuffer(buffer)));
	}
}
