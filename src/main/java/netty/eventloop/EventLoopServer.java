package netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class EventLoopServer {
    public static void main(String[] args) {
        // 创建一个独立的 group 去处理业务部分,防止由于业务代码逻辑过长导致整体读写 group 阻塞
        EventLoopGroup group = new DefaultEventLoop();
        new ServerBootstrap()
                // boss,worker
                // 第一个参数只负责 accept, 第二个负责读写
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("default-group", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                System.out.println(byteBuf.toString(Charset.forName("UTF-8")));
                                super.channelRead(ctx, msg);
                            }
                        });

                        ch.pipeline().addLast(group, "business-group", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                System.out.println(byteBuf.toString(Charset.forName("UTF-8")));
                            }
                        });
                    }
                }).bind(9922);
    }
}
