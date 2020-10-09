package wei.yigulu.cdt.cdtframe;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wei.yigulu.utils.CrcUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * cdt数据帧的模型
 *
 * @author 修唯xiuwei
 **/
@NoArgsConstructor
@Getter
public class CDTFrameBean {

	/**
	 * 同步字
	 */
	private final byte[] HEAD = new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0xEB, (byte) 0x90, (byte) 0xEB, (byte) 0x90};

	/**
	 * 控制字节 b7 默认0x71
	 */
	@Setter
	private byte control = 0x71;

	/**
	 * 帧类别码 b8
	 */
	@Setter
	private CDTType cdtType;

	/**
	 * 信息字数 b9  信息字的个数   即dates的长度
	 */
	private int num;

	/**
	 * 源地址 b10
	 */
	private int sourceAddress = 1;

	/**
	 * 目的地址 b11
	 */
	private int destinationAddress = 1;


	/**
	 * 校验码 b12
	 */
	private int crc;

	private List<BaseDateType> dates = new ArrayList<>();

	public CDTFrameBean(ByteBuf byteBuf) throws InstantiationException, IllegalAccessException {
		loadBytes(byteBuf);
	}

	public CDTFrameBean(List<BaseDateType> dates) {
		if (dates.size() > 0) {
			this.dates = dates;
			this.num = dates.size();
			if (dates.get(0) instanceof IntegerDataType) {
				this.cdtType = CDTType.COMMONYC;
			} else {
				this.cdtType = CDTType.YX;
			}
		}


	}


	public void loadBytes(ByteBuf byteBuf) throws IllegalAccessException, InstantiationException {
		byte[] bs = new byte[5];
		byteBuf.readBytes(bs);
		if (this.control != bs[0]) {
			return;
		}
		this.crc = byteBuf.readByte();
		if ((this.crc & 0xff) != CrcUtils.generateCRC8(bs)) {
			return;
		}
		this.cdtType = CDTType.getByNo(bs[1]);
		if (this.cdtType == null) {
			return;
		}
		this.num = Math.abs(bs[2]);
		this.sourceAddress = bs[3];
		this.destinationAddress = bs[4];
		BaseDateType dateType;
		this.dates = new ArrayList<>(this.num);
		for (int i = 0; i < this.num; i++) {
			dateType = (BaseDateType) this.cdtType.typeClass.newInstance();
			dateType.loadBytes(byteBuf);
			dates.add(dateType);
		}
	}


	public byte[] encode() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.put(HEAD);
		byte[] bs = new byte[]{this.control, (byte) this.cdtType.no, (byte) this.num, (byte) this.sourceAddress, (byte) this.destinationAddress};
		byteBuffer.put(bs);
		byteBuffer.put((byte) CrcUtils.generateCRC8(bs));
		for (BaseDateType data : dates) {
			data.encode(byteBuffer);
		}
		byte[] bytes = new byte[byteBuffer.position()];
		byteBuffer.rewind();
		byteBuffer.get(bytes);
		return bytes;
	}


	@Override
	public String toString() {

		String s = "\n----------------------------------\n";
		if (this.cdtType != null) {
			s += "数据类型：" + this.cdtType.name + "\n";
		}
		s += "源地址：" + this.sourceAddress + "\n";
		s += "目标地址：" + this.destinationAddress + "\n";
		if (this.dates != null) {
			for (BaseDateType<?> b : this.dates) {
				s += b.toString();
			}
		}
		return s;
	}
}
