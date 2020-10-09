package wei.yigulu.modbus.domain;

import lombok.AllArgsConstructor;

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
	/**
	 * Constant <code>READ_DISCRETE_INPUTS 2</code>
	 */
	READ_DISCRETE_INPUTS(2),
	/**
	 * Constant <code>READ_HOLDING_REGISTERS 3</code>
	 */
	READ_HOLDING_REGISTERS(3),
	/**
	 * Constant <code>READ_INPUT_REGISTERS 4</code>
	 */
	READ_INPUT_REGISTERS(4),
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

	private Integer code;


}
