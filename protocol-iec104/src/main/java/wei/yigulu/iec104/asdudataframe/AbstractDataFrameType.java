package wei.yigulu.iec104.asdudataframe;


import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import wei.yigulu.iec104.apdumodel.Apdu;
import wei.yigulu.iec104.apdumodel.Asdu;
import wei.yigulu.iec104.apdumodel.Vsq;

import java.util.List;

/**
 * ASDU 数据内容的基类
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
public abstract class AbstractDataFrameType {

	/**
	 * Work 4 builders
	 */
	@Getter
	@Setter
	protected List<String> work4Builders;


	/**
	 * 读取ByteBuf组装DataType
	 *
	 * @param is  is
	 * @param vsq vsq
	 */
	public abstract void loadByteBuf(ByteBuf is, Vsq vsq);


	/**
	 * 由DataType编码为byte的list
	 *
	 * @param buffer buffer
	 */
	public abstract void encode(List<Byte> buffer);

	/**
	 * 由DataType（asdu的数据部分） 新建Asdu
	 *
	 * @return asdu
	 */
	public abstract Asdu generateBack();


	/**
	 * 用以对该格式帧进行处理
	 *
	 * @param apdu apdu
	 * @return byte [ ] [ ]
	 * @throws Exception exception
	 */
	public abstract byte[][] handleAndAnswer(Apdu apdu) throws Exception;


}
