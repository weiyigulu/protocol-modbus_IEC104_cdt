package wei.yigulu.modbus.domain.datatype;


import lombok.AllArgsConstructor;
import lombok.Getter;
import wei.yigulu.modbus.domain.datatype.numeric.*;

/**
 * modbus数据类型的枚举
 *
 * @author: xiuwei
 */
@AllArgsConstructor
public enum ModbusDataTypeEnum {


	/**
	 * modbus 各种类型
	 */
	A16(1),
	/*AB 的modbus 2 字节 无符号 整型数据*/
	P_AB(1),
	/*BA 的modbus 2 字节 无符号 整型数据*/
	P_BA(1),
	/*AB 的modbus 2 字节 有符号 整型数据*/
	PM_AB(1),
	/*BA 的modbus 2 字节 有符号 整型数据*/
	PM_BA(1),
	/*AABB 的modbus 4 字节 无符号 整型数据*/
	P_ABCD(2),
	/*BBAA 的modbus 4 字节 无符号 整型数据*/
	P_CDAB(2),
	/*AABB 的modbus 4 字节 有符号 整型数据*/
	PM_ABCD(2),
	/*BBAA 的modbus 4 字节 有符号 整型数据*/
	PM_CDAB(2),
	/*ABCD 的modbus 4 字节  浮点数据*/
	ABCD(2),
	/*BADC 的modbus 4 字节  浮点数据*/
	BADC(2),
	/*CDAB 的modbus 4 字节  浮点数据*/
	CDAB(2),
	/*DCBA 的modbus 4 字节  浮点数据*/
	DCBA(2);


	/**
	 * 占据寄存器数量
	 */
	@Getter
	private Integer occupiedRegister;

	/**
	 * 根据本枚举类型返回相对数据对象
	 *
	 * @return
	 */
	public IModbusDataType getObject() {
		switch (this) {
			case P_BA:
				return new P_BA();
			case PM_AB:
				return new PM_AB();
			case PM_BA:
				return new PM_BA();
			case ABCD:
				return new ABCD();
			case P_ABCD:
				return new P_ABCD();
			case P_CDAB:
				return new P_CDAB();
			case PM_ABCD:
				return new PM_ABCD();
			case PM_CDAB:
				return new PM_CDAB();
			case CDAB:
				return new CDAB();
			case DCBA:
				return new DCBA();
			case BADC:
				return new BADC();
			case A16:
				return new BooleanModbusDataInRegister();
			default:
				return new P_AB();
		}
	}


}
