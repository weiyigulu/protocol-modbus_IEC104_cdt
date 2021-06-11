package wei.yigulu.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.experimental.Accessors;
import wei.yigulu.utils.DataConvertor;


/**
 * 主站  向子站发送召唤 获取子站的数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

@Accessors(chain = true)
public abstract class AbstractMasterBuilder extends AbstractClientBuilder implements MasterInterface {


	/**
	 * 向对端 发送数据帧
	 *
	 * @param bytes
	 */
	@Override
	public void sendFrameToOpposite(byte[] bytes) {
		if (getFuture() != null && getFuture().channel().isActive()) {
			getLog().info("se ==> " + DataConvertor.Byte2String(bytes));
			getFuture().channel().writeAndFlush(Unpooled.copiedBuffer(bytes));
		}else{
			throw new RuntimeException("无客户端连接");
		}
	}

	/**
	 * 向对端 发送数据帧
	 *
	 * @param byteBuf
	 */
	@Override
	public void sendFrameToOpposite(ByteBuf byteBuf) {
		if (getFuture() != null && getFuture().channel().isActive()) {
			getLog().info("se ==> " + DataConvertor.ByteBuf2String(byteBuf));
			getFuture().channel().writeAndFlush(Unpooled.copiedBuffer(byteBuf));
		}else{
			throw new RuntimeException("无客户端连接");
		}
	}


}
