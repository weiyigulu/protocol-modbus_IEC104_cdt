import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 测试客户端
 *
 * @author 修唯xiuwei
 * @create 2018-02-05 15:56
 * @Email 524710549@qq.com
 **/

public class Server4 {
	public static void main(String[] args) {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();

		try {
			//辅助启动类
			ServerBootstrap bootstrap = new ServerBootstrap();
			//设置线程池
			bootstrap.group(boss, worker);

			//设置socket工厂
			bootstrap.channel(NioServerSocketChannel.class);

			//设置管道工厂
			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					//获取管道
					ChannelPipeline pipeline = socketChannel.pipeline();
					//字符串解码器
					pipeline.addLast(new StringDecoder());
					//字符串编码器
					pipeline.addLast(new StringEncoder());
					//处理类
					pipeline.addLast(new ServerHandler4());
				}
			});

			//设置TCP参数
			//1.链接缓冲池的大小（ServerSocketChannel的设置）
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			//维持链接的活跃，清除死链接(SocketChannel的设置)
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			//关闭延迟发送
			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

			//绑定端口
			ChannelFuture future = bootstrap.bind(9001).sync();
			System.out.println("server start ...... ");

			//等待服务端监听端口关闭
			future.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//优雅退出，释放线程池资源
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

}


class ServerHandler4 extends SimpleChannelInboundHandler<String> {

	//读取客户端发送的数据
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("client response :" + msg);
	}

	//新客户端接入
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive");
	}

	//客户端断开
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelInactive");
	}

	//异常
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//关闭通道
		ctx.channel().close();
		//打印异常
		cause.printStackTrace();
	}
}
