package wei.yigulu.cdt.netty;

import lombok.Getter;
import wei.yigulu.cdt.cdtframe.AbstractCDTDataTransmitter;
import wei.yigulu.netty.AbstractRtuModeBuilder;
import wei.yigulu.netty.ProtocolChannelInitializer;
import wei.yigulu.purejavacomm.PureJavaCommChannel;


/**
 * cdt
 *
 * @author: xiuwei
 * @version:
 */
public class CDTSlaver extends AbstractRtuModeBuilder {

	@Getter
	private final AbstractCDTDataTransmitter dataTransmitter;

	public CDTSlaver(String commPortId, AbstractCDTDataTransmitter dataTransmitter) {
		super(commPortId);
		this.dataTransmitter = dataTransmitter;
	}


	@Override
	protected ProtocolChannelInitializer getOrCreateChannelInitializer() {
		return new ProtocolChannelInitializer<PureJavaCommChannel>(this) {

			@Override
			protected void initChannel(PureJavaCommChannel ch) throws Exception {
				ch.pipeline().addLast(new SlaverHandler((CDTSlaver) builder));
			}
		};
	}
}
