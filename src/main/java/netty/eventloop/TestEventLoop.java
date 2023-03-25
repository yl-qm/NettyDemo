package netty.eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.TimeUnit;

public class TestEventLoop {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(2); // io, 普通任务, 定时任务
        // EventLoopGroup group1 = new DefaultEventLoop(); // 普通任务, 定时任务

        // 获取下次的 eventLoop
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 执行普通任务
        group.submit(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("ok");
        });

        // 执行定时任务
        // 当前间隔0S后执行,每隔1S后再次执行
        group.next().scheduleAtFixedRate(()->{
            System.out.println("go");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
