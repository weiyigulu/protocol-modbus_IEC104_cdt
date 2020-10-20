package wei.yigulu.modbus.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能码 copy from modbus4j
 *
 * @author: xiuwei
 * @version:
 */
@AllArgsConstructor
public enum FunctionCode {
	/**
	 * Constant <code>READ_COILS 1</code>
	 */
	READ_COILS(1),
	READ_COILS_ERROR(0x81),
	/**
	 * Constant <code>READ_DISCRETE_INPUTS 2</code>
	 */
	READ_DISCRETE_INPUTS(2),
	READ_DISCRETE_INPUTS_ERROR(0x82),
	/**
	 * Constant <code>READ_HOLDING_REGISTERS 3</code>
	 */
	READ_HOLDING_REGISTERS(3),
	READ_HOLDING_REGISTERS_ERROR(0x83),
	/**
	 * Constant <code>READ_INPUT_REGISTERS 4</code>
	 */
	READ_INPUT_REGISTERS(4),
	READ_INPUT_REGISTERS_ERROR(0x84),
	/**
	 * Constant <code>WRITE_COIL 5</code>
	 */
	WRITE_COIL(5),
	/**
	 * Constant <code>WRITE_REGISTER 6</code>
	 */
	WRITE_REGISTER(6),
	/**
	 * Constant <code>READ_EXCEPTION_STATUS 7</code>
	 */
	READ_EXCEPTION_STATUS(7),
	/**
	 * Constant <code>WRITE_COILS 15</code>
	 */
	WRITE_COILS(15),
	/**
	 * Constant <code>WRITE_REGISTERS 16</code>
	 */
	WRITE_REGISTERS(16),
	/**
	 * Constant <code>REPORT_SLAVE_ID 17</code>
	 */
	REPORT_SLAVE_ID(17),
	/**
	 * Constant <code>WRITE_MASK_REGISTER 22</code>
	 */
	WRITE_MASK_REGISTER(22);


	@Getter
	private Integer code;


	public static FunctionCode valueOf(int code){
		switch (code){
			case 1:return READ_COILS;
			case 2:return READ_DISCRETE_INPUTS;
			case 3:return READ_HOLDING_REGISTERS;
			case 4:return READ_INPUT_REGISTERS;
			case 0x81:return READ_COILS_ERROR;
			case 0x82:return READ_DISCRETE_INPUTS_ERROR;
			case 0x83:return READ_HOLDING_REGISTERS_ERROR;
			case 0x84:return READ_INPUT_REGISTERS_ERROR;
			case 5:return WRITE_COIL;
			case 6:return WRITE_REGISTER;
			case 7:return READ_EXCEPTION_STATUS;
			case 15:return WRITE_COILS;
			case 16:return WRITE_REGISTERS;
			case 17:return REPORT_SLAVE_ID;
			case 22:return WRITE_MASK_REGISTER;
			default:throw  new IllegalArgumentException();
		}

	}
}
