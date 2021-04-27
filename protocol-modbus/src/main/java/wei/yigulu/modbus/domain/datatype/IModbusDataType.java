package wei.yigulu.modbus.domain.datatype;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * modbus的数据类型的抽象类
 *
 * @author: xiuwei
 * @version:
 */
public interface IModbusDataType {


	/**
	 * 解码
	 *
	 * @param bytes  字节
	 * @param offset 偏移量 偏移量是相对寄存器讲的
	 */
	IModbusDataType decode(byte[] bytes, int offset);

	/**
	 * 解码
	 *
	 * @param byteBuf 字节缓冲区
	 * @return {@link IModbusDataType}
	 */
	IModbusDataType decode(ByteBuffer byteBuf);


	/**
	 * 编码
	 *
	 * @param bytes 字节
	 */
	public abstract IModbusDataType encode(List<Byte> bytes);


}
