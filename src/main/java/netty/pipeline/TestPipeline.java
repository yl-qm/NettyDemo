package netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加处理器, netty 会自动创建一个 head 和 tail 分别在头尾, 新加入的在他们中间
                        // 处理器执行顺序 head -> h1 -> h2 -> h3 -> h6 -> h5 -> h4 -> tail
                        // super.channelRead(ctx, s) 是向下一个 Handler 执行的意思
                        // 类似于过滤器中的 preHandler
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("1");
                                ByteBuf byteBuf = (ByteBuf) msg;
                                String s = byteBuf.toString(Charset.defaultCharset());
                                super.channelRead(ctx, s);
                            }
                        });
                        pipeline.addLast("h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("2");
                                super.channelRead(ctx, msg);
                            }
                        });
                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("3");
                                super.channelRead(ctx, msg);
                                // ctx 调用 writeAndFlush 后, 从h3直接掉头去找h2处理器了
                                // ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("123".getBytes()));
                                // ch 是执行完所有入栈处理器后到tail,然后从tail开始找出栈处理器
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("123".getBytes()));
                            }
                        });
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                System.out.println("6");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                })
                .bind(9779);
    }
}
