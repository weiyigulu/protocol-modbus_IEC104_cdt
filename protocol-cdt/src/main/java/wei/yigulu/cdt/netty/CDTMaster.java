package wei.yigulu.cdt.netty;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.Getter;
import wei.yigulu.cdt.cdtframe.AbstractCDTDataHandler;
import wei.yigulu.netty.AbstractRtuModeBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;
import wei.yigulu.purejavacomm.PureJavaCommChannel;


/**
 * cdt读取端
 *
 * @author 修唯xiuwei
 **/
public class CDTMaster extends AbstractRtuModeBuilder {

	private static final int MAXLEN = 10240;

	private final byte[] HEAD = new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0xEB, (byte) 0x90, (byte) 0xEB, (byte) 0x90};

	@Getter
	private final AbstractCDTDataHandler dataHandler;

	public CDTMaster(String commPortId, AbstractCDTDataHandler dataHandler) {
		super(commPortId);
		this.dataHandler = dataHandler;
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		return new ProtocolChannelInitializer<PureJavaCommChannel>(this) {
			@Override
			protected void initChannel(PureJavaCommChannel ch) throws Exception {
				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(MAXLEN, Unpooled.copiedBuffer(HEAD)));
				ch.pipeline().addLast(new MasterHandler((CDTMaster) builder));
			}
		};
	}
}
