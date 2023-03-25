package netty.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;


public class HelloServer {
    public static void main(String[] args) {
        // 创建一个启动器,装配netty组件
        new ServerBootstrap()
                // 事件组
                // eventLoop 类似于之前多线程的 selector ,但是他这里强制一个 selector 对应一个或者多个 channel
                // 之前在手写多线程NIO的时候 selector 和 channel 我们可以随意匹配的。 这里可以方便管理
                .group(new NioEventLoopGroup())
                // 选择服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // boss 负责处理连接 work(child) 负责处理具体事件,里面的代码决定了 work(child) 能完成哪些操作(handler)
                .childHandler(
                        // 通道初始化方法,负责添加其他的 handler
                        new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            // 新建一个 handler 用于把 ByteBuf 转换为 String
                            nioSocketChannel.pipeline().addLast(new StringDecoder());
                            // 新建一个 handler 用于添加自己自定义的方法 Adapter(适配器)
                            nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("before :" + msg);
                                    System.out.println("after :" + msg);
                                }
                            });
                    }
                })
                // 绑定具体端口
                .bind(10021);
    }
}
