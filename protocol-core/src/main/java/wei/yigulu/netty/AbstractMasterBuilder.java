package wei.yigulu.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.threadpool.LocalThreadPool;
import wei.yigulu.utils.DataConvertor;


/**
 * 主站  向子站发送召唤 获取子站的数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */

@Accessors(chain = true)
public abstract class AbstractMasterBuilder extends BaseProtocolBuilder {


	/**
	 * Work group
	 */
	protected EventLoopGroup workGroup = null;
	/**
	 * Bootstrap
	 */
	protected Bootstrap bootstrap = null;

	/**
	 * Future
	 */
	@Getter
	@Setter
	protected ChannelFuture future;
	/**
	 * Connection listener
	 */
	protected ChannelFutureListener connectionListener = null;


	protected ProtocolChannelInitializer channelInitializer = null;


	public void stop() {
		if (this.future != null) {
			this.future.removeListener(getOrCreateConnectionListener());
			if (!this.future.channel().eventLoop().isShutdown()) {
				this.future.channel().close();
			}
		}
		if (this.workGroup != null) {
			this.workGroup.shutdownGracefully();
		}
		this.bootstrap=null;
		this.workGroup=null;
	}


	/**
	 * 向对端 发送数据帧
	 *
	 * @param bytes
	 */
	public void sendFrameToOpposite(byte[] bytes) {
		if (getFuture() != null && getFuture().channel().isActive()) {
			getLog().info("se ==> " + DataConvertor.Byte2String(bytes));
			getFuture().channel().writeAndFlush(Unpooled.copiedBuffer(bytes));
		}
	}

	/**
	 * 向对端 发送数据帧
	 *
	 * @param byteBuf
	 */
	public void sendFrameToOpposite(ByteBuf byteBuf) {
		if (getFuture() != null && getFuture().channel().isActive()) {
			getLog().info("se ==> " + DataConvertor.ByteBuf2String(byteBuf));
			getFuture().channel().writeAndFlush(Unpooled.copiedBuffer(byteBuf));
		}
	}

	/**
	 * 创建Master 连接
	 */
	public  abstract void create();

	/**
	 * Create by un block
	 */
	public void createByUnBlock() {
		LocalThreadPool.getInstance().getLocalPool().execute(this::create);
	}


	/**
	 * null则创建，有则获取获取EventLoopGroup 用与bootstrap的绑定
	 *
	 * @return or create work group
	 */
	public abstract EventLoopGroup getOrCreateWorkGroup();


	/**
	 * null则创建，有则获取获取bootstrap
	 *
	 * @return or create bootstrap
	 */
	public abstract Bootstrap getOrCreateBootstrap();


	/**
	 * null则创建，有则获取获取ConnectionListener
	 *
	 * @return or create connection listener
	 */
	public abstract ChannelFutureListener getOrCreateConnectionListener();

	/**
	 * null则创建，有则获取获取ChannelInitializer
	 *
	 * @return or create ChannelInitializer
	 */
	protected abstract ProtocolChannelInitializer getOrCreateChannelInitializer();

	/**
	 * 通道连接成功
	 */
	public void connected() {

	}

	/**
	 * 通道断开连接
	 */
	public void disconnected() {

	}


}
