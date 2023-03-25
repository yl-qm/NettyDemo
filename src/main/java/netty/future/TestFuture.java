package netty.future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // jdkFuture();
        // nettyFuture();
        nettyPromise();
    }

    static void jdkFuture() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);

        Future<Integer> submit = service.submit(() -> {
            System.out.println("计算");
            Thread.sleep(1000);
            return 50;
        });

        System.out.println("等待");
        System.out.println("结果：" + submit.get());
    }

    static void nettyFuture() throws ExecutionException, InterruptedException {
        // 有多个 NioEventLoop ,每个 NioEventLoop 都只有一个线程
        NioEventLoopGroup group = new NioEventLoopGroup();
        EventLoop eventLoop = group.next();

        io.netty.util.concurrent.Future<Integer> future = eventLoop.submit(() -> {
            System.out.println("计算");
            Thread.sleep(5000);
            return 50;
        });

        // System.out.println("等待");
        // System.out.println("结果：" + future.get());

        // 异步
        future.addListener(new GenericFutureListener<io.netty.util.concurrent.Future<? super Integer>>() {
            @Override
            public void operationComplete(io.netty.util.concurrent.Future<? super Integer> future) throws Exception {
                System.out.println("结果：" + future.getNow());
            }
        });
    }

    static void nettyPromise() throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        eventLoop.submit(()->{
            System.out.println("计算");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(20);
        });

        System.out.println(promise.get());
    }
}
