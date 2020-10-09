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
public class NettyClient {
	public static void main(String[] args) {

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap sb = new ServerBootstrap();
			// 绑定线程池
			sb.group(group)
					// 指定使用的channel
					.channel(NioServerSocketChannel.class)
					// 绑定监听端口
					.localAddress(9001)
					// 绑定客户端连接时候触发操作
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new StringDecoder());
							//字符串编码器
							ch.pipeline().addLast(new StringEncoder());
							//处理类
							ch.pipeline().addLast(new ClientHandler4());
						}
					});
			// 服务器异步创建绑定
			ChannelFuture cf = sb.bind().sync();
			// 关闭服务器通道
			cf.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放线程池资源
				group.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//worker负责读写数据

	}

}

class ClientHandler4 extends SimpleChannelInboundHandler<String> {

	//接受服务端发来的消息
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println("server response ： " + msg);
	}

	//与服务器建立连接
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//给服务器发消息
		ctx.channel().writeAndFlush("i am client !\n");

		System.out.println("channelActive");
	}

	//与服务器断开连接
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelInactive");
	}

	//异常
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//关闭管道
		ctx.channel().close();
		//打印异常信息
		cause.printStackTrace();
	}
}



