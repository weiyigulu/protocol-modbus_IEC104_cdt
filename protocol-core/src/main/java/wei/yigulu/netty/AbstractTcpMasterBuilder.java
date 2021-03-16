package wei.yigulu.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * TCP主站  向子站发送总召唤 获取子站的数据
 * <p>
 * 简单的主站  相对于主备机主站    仅有主机 不支持切换
 *
 * @author 修唯xiuwei
 * @version 3.0
 */
@Accessors(chain = true)
public abstract class AbstractTcpMasterBuilder extends AbstractMasterBuilder {


	/**
	 * 对端slave的ip
	 */
	@Getter
	String ip;


	/**
	 * 对端slave的端口
	 */
	@Getter
	Integer port;


	/**
	 * 本端的ip
	 */
	@Getter
	@Setter
	private String selfIp;

	/**
	 * 本端的端口
	 */
	@Getter
	@Setter
	private Integer selfPort;

	/**
	 * 构造方法
	 *
	 * @param ip   ip
	 * @param port port
	 */
	public AbstractTcpMasterBuilder(String ip, Integer port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public void create() {
		synchronized (this) {
			if (future != null) {
				future.removeListener(getOrCreateConnectionListener());
				if (!future.channel().eventLoop().isShutdown()) {
					future.channel().close();
				}
				future = null;
			}
			log.debug("创建连接");
			try {
				SocketAddress remoteAddress= new InetSocketAddress(getIp(),getPort());
				if(!StringUtil.isNullOrEmpty(getSelfIp()) && getSelfPort()!=null){
					SocketAddress localAddress= new InetSocketAddress(getSelfIp(),getSelfPort());
					future = getOrCreateBootstrap().connect(remoteAddress,localAddress);
				}else{
					future = getOrCreateBootstrap().connect(remoteAddress);
				}
				log.debug("为连接添加监听");
				future.addListener(getOrCreateConnectionListener());
			} catch (Exception e) {
				log.debug("创建连接时发生异常");
				try {
					this.connectionListener.operationComplete(future);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return;
			}
		}
		try {
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public EventLoopGroup getOrCreateWorkGroup() {
		if (this.workGroup == null || this.workGroup.isShutdown()) {
			this.workGroup = new NioEventLoopGroup();
		}
		return this.workGroup;
	}


	@Override
	public Bootstrap getOrCreateBootstrap() {
		if (this.bootstrap == null) {
			bootstrap = new Bootstrap();
			bootstrap.group(getOrCreateWorkGroup())
					.channel(NioSocketChannel.class)
					.handler(getOrCreateChannelInitializer());
			bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
			bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
		}
		return this.bootstrap;
	}

	@Override
	public ChannelFutureListener getOrCreateConnectionListener() {
		if (this.connectionListener == null) {
			this.connectionListener = new SimpleTcpConnectionListener(this);
		}
		return this.connectionListener;
	}

	/**
	 * 获取或创建通道初始化
	 *
	 * @return {@link ProtocolChannelInitializer}
	 */
	@Override
	protected abstract ProtocolChannelInitializer getOrCreateChannelInitializer();


	/**
	 * 重新建立loopgroup 关闭线程池中负责的所有链接
	 * 若是公用loopgroup 则可不做任何动作或者 谨慎启停所有链接
	 *
	 * @return event loop group
	 */
	public EventLoopGroup refreshLoopGroup() {
		try {
			if (!this.workGroup.isShutdown()) {
				this.workGroup.shutdownGracefully();
				log.debug("刷新连接线程，关闭后重启");
			}
			this.workGroup = new NioEventLoopGroup();
			this.bootstrap = null;
			log.error(" TCP Master 断开重连");
		} catch (Exception e) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return refreshLoopGroup();
		}
		return this.workGroup;
	}



}
