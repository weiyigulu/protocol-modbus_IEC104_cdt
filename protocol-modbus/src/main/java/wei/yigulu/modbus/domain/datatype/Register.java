package wei.yigulu.modbus.domain.datatype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 寄存器的对象 两个字节
 *
 * @author: xiuwei
 * @version:
 */
@AllArgsConstructor
@NoArgsConstructor
public class Register {

	@Getter
	byte b1 = 0;
	@Getter
	byte b2 = 0;


}
