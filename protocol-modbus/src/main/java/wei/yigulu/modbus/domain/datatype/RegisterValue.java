package wei.yigulu.modbus.domain.datatype;

import lombok.Getter;

import java.util.List;

/**
 * 寄存器值
 *
 * @author: xiuwei
 * @version:
 */
public abstract class RegisterValue implements IModbusDataType {

	protected static final int BYTEINREGISTER = 2;

	@Getter
	protected ModbusDataTypeEnum modbusDataTypeEnum = ModbusDataTypeEnum.P_AB;

	/**
	 * 转化成register字串
	 *
	 * @return {@link List<Register>}
	 */
	public abstract List<Register> getRegisters();

}
