package wei.yigulu.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wei.yigulu.connectfilterofslave.ConnectFilterManager;
import wei.yigulu.threadpool.LocalThreadPool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * TCP网络传输层的server
 * 向主站上送数据
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public abstract class AbstractTcpServerBuilder extends BaseProtocolBuilder {


	private EventLoopGroup group;

	private ServerBootstrap serverBootstrap;

	@Setter
	@Getter
	private int port = 2404;

	@Setter
	@Getter
	private String ip = null;


	/**
	 * 连接过滤器管理器
	 */
	@Setter
	@Getter
	private ConnectFilterManager connectFilterManager = new ConnectFilterManager();

	protected ProtocolChannelInitializer channelInitializer = null;

	/**
	 * 子channel集合
	 */
	@Getter
	private List<Channel> channels = new ArrayList<>();
	/**
	 * 父channel
	 */
	@Getter
	private Channel fatherChannel;


	public AbstractTcpServerBuilder(int port) {
		this.port = port;
	}

	/**
	 * 创建104 slave 监听
	 *
	 * @throws Exception 异常
	 */
	public void create() throws Exception {
		// 服务器异步创建绑定
		ChannelFuture cf = getOrCrateServerBootstrap().bind().sync();
		this.fatherChannel = cf.channel();
		log.info("Slaver端启动成功；端口" + port);
		// 关闭服务器通道
		cf.channel().closeFuture().sync();
		// 释放线程池资源
		group.shutdownGracefully().sync();
	}


	/**
	 * null则创建，有则获取获取ChannelInitializer
	 *
	 * @return or create ChannelInitializer
	 */
	protected abstract ProtocolChannelInitializer getOrCreateChannelInitializer();

	/**
	 * 获取ServerBootstrap
	 * 如果==null 那么就创建
	 *
	 * @return
	 */
	protected ServerBootstrap getOrCrateServerBootstrap() {
		if (this.serverBootstrap == null) {
			AbstractTcpServerBuilder slaverBuilder = this;
			this.serverBootstrap = new ServerBootstrap();
			// 绑定线程池
			this.serverBootstrap.group(getOrCrateLoopGroup())
					// 指定使用的channel
					.channel(NioServerSocketChannel.class)
					// 绑定客户端连接时候触发操作
					.childHandler(getOrCreateChannelInitializer());
			// 绑定监听端口
			if (this.ip != null) {
				this.serverBootstrap.localAddress(this.ip, this.port);
			} else {
				this.serverBootstrap.localAddress(this.port);
			}
		}
		return this.serverBootstrap;
	}

	/**
	 * 获取或创建 循环任务组
	 *
	 * @return {@link EventLoopGroup}
	 */
	protected EventLoopGroup getOrCrateLoopGroup() {
		if (this.group == null) {
			this.group = new NioEventLoopGroup();
		}
		return this.group;
	}


	/**
	 * 停止通道监听
	 */
	public void stop() {
		if (this.fatherChannel != null) {
			this.fatherChannel.close();
			fatherChannel = null;
		}
		for (Channel c : this.channels) {
			c.close();
		}
		this.channels = new ArrayList<>();
		if (this.group != null) {
			this.group.shutdownGracefully();
		}

	}

	/**
	 * 以非阻塞的方式启动
	 */
	public void createByUnBlock() {
		AbstractTcpServerBuilder s = this;
		LocalThreadPool.getInstance().getLocalPool().execute(() -> {
			try {
				s.create();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	/**
	 * 当有连接接入时触发
	 *
	 * @param ipSocket ip套接字
	 */
	public void connected(InetSocketAddress ipSocket) {

	}

	/**
	 * 当有连接断开时触发
	 *
	 * @param ipSocket ip套接字
	 */
	public void disconnected(InetSocketAddress ipSocket) {

	}

}
