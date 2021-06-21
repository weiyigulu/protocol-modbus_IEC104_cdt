package wei.yigulu.modbus.netty;

import com.alibaba.fastjson.JSON;
import com.google.common.primitives.Bytes;
import wei.yigulu.modbus.domain.FunctionCode;
import wei.yigulu.modbus.domain.ModbusSlaveDataContainer;
import wei.yigulu.modbus.domain.command.AbstractModbusCommand;
import wei.yigulu.modbus.domain.confirm.AbstractModbusConfirm;
import wei.yigulu.modbus.domain.datatype.RegisterValue;
import wei.yigulu.modbus.domain.datatype.UnknownTypeRegisterValue;
import wei.yigulu.modbus.domain.datatype.numeric.P_AB;
import wei.yigulu.modbus.exceptiom.ModbusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * modbus 子站端的接口
 *
 * @author: xiuwei
 * @version:
 */
public interface ModbusSlaverInterface {

	/**
	 * 获取modbus子站的数据容器
	 *
	 * @return {@link ModbusSlaveDataContainer}
	 */
	public ModbusSlaveDataContainer getModbusSlaveDataContainer();


	/**
	 * 接收命令
	 *
	 * @param command 命令
	 * @return boolean
	 */
	public boolean receiveCommand(AbstractModbusCommand command);


	/**
	 * 接收命令并回答
	 *
	 * @param command               命令
	 * @param confirm modbus确认
	 * @return {@link AbstractModbusConfirm}
	 */
	public  default byte[] receiveCommandAndAnswer(AbstractModbusCommand command,AbstractModbusConfirm confirm) throws ModbusException {
		List<RegisterValue> list=new ArrayList<>();
		for(int i=0;i<command.getDataBytes().length;i+=2){
			list.add(new UnknownTypeRegisterValue().decode(command.getDataBytes(),i));
		}
		if(receiveCommand(command)) {
			getModbusSlaveDataContainer().setRegister(command.getSlaveId(), command.getStartAddress(), list);
			confirm.setSlaveId(command.getSlaveId())
					.setFunctionCode(command.getFunctionCode())
					.setQuantity(command.getQuantity())
					.setStartAddress(command.getStartAddress());
			if(command.getFunctionCode()== FunctionCode.WRITE_COIL || command.getFunctionCode()==FunctionCode.WRITE_REGISTER){
				confirm.setB2(command.getDataBytes());
			}else{
				confirm.getB2()[0]=(byte)(command.getQuantity() >> 8);
				confirm.getB2()[1]=(byte)command.getQuantity().intValue();
			}
			List<Byte> bl=new ArrayList<>();
			confirm.encode(bl);
			return Bytes.toArray(bl);
		}
		System.out.println(JSON.toJSONString(command));
		return null;
	}

}
