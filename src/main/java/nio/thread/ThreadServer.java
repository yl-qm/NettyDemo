package nio.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("BOSS");

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(9111));
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);
        // 创建work池
        Worker worker = new Worker("worker-0");

        while (true){
           boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel channel = ssc.accept();
                    channel.configureBlocking(false);
                    System.out.println("before connect..."+channel);
                    worker.register(channel);
                    System.out.println("after connect..."+channel);
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;
        private Selector worker;
        private String name;

        private volatile boolean start = false;
        // 创建同步队列
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name){
            this.name = name;
        }

        public void register(SocketChannel channel) throws IOException {
            if (!start){
                worker = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            // 先创建任务,再唤醒worker让其不再堵塞
            queue.add(()->{
                try {
                    channel.register(worker, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            worker.wakeup();
        }

        @Override
        public void run() {
            while (true){
                try {
                    worker.select();
                    Iterator<SelectionKey> iterator = worker.selectedKeys().iterator();
                    Runnable task = queue.poll();
                    if (task != null){
                        task.run();
                    }
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                            int read = channel.read(byteBuffer);
                            if (read > 0){
                                byteBuffer.flip();
                                while (byteBuffer.hasRemaining()){
                                    System.out.println((char) byteBuffer.get());
                                }
                                byteBuffer.clear();
                                System.out.println("读取后");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
