package netty.eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        // 带有 future,promise 都是配合异步,多线程使用的
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 连接之后调用
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        nioSocketChannel.pipeline().addLast(new StringDecoder());
                    }
                })
                // 这个连接是异步非阻塞的, main 线程创建好之后发起调用, 真正执行 channel 的线程是 NioEventLoopGroup
                .connect(new InetSocketAddress("localhost", 9779)); // 可能连接需要1S

        /*
        // 第一种方法
        // 假设没有执行 sync() 方法
        ChannelFuture future = channelFuture.sync();    // 阻塞线程,等待 nio 线程建立完成再向下执行

        // 这里就只有一个 main 线程,这时候 channel 是没有连接的,并不能获取到
        Channel channel = future.channel();
        channel.writeAndFlush(Unpooled.buffer().writeBytes("23123".getBytes()));
        */

        // 第二种方法
        // 使用 addListener 回调异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush(Unpooled.buffer().writeBytes("23123".getBytes()));

                new Thread(()->{
                    Scanner scanner = new Scanner(System.in);
                    while (true){
                        String line = scanner.nextLine();
                        if ("q".equals(line)){
                            channel.close();
                            break;
                        }
                        channel.writeAndFlush(Unpooled.buffer().writeBytes(line.getBytes()));
                    }
                }).start();

                ChannelFuture closeFuture = channel.closeFuture();
                // closeFuture.sync();
                // group.shutdownGracefully();
                // System.out.println("彻底关闭");
                closeFuture.addListener((ChannelFutureListener) future1 -> {
                    group.shutdownGracefully();
                    System.out.println("彻底关闭");
                });
            }
        });




        // System.out.println("stop");
        // System.out.println("");
    }
}
