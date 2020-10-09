package wei.yigulu.modbus.domain.datatype;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 数字的类型
 *
 * @author: xiuwei
 * @version:
 */
@NoArgsConstructor
public abstract class NumericModbusData extends RegisterValue {

	public NumericModbusData(BigDecimal value) {
		this.value = value;
	}

	@Setter
	@Getter
	@Accessors(chain = true)
	protected BigDecimal value;

	/**
	 * 解码
	 *
	 * @param bytes  字节
	 * @param offset 偏移量 偏移量是相对寄存器讲的
	 */
	@Override
	public abstract IModbusDataType decode(byte[] bytes, int offset);

	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲区
	 * @return {@link IModbusDataType}
	 */
	@Override
	public abstract IModbusDataType decode(ByteBuffer byteBuf);


	/**
	 * 编码
	 *
	 * @param bytes 字节
	 */
	@Override
	public abstract IModbusDataType encode(List<Byte> bytes);

}
