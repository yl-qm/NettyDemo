package nio.blocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        // 使用nio写一个阻塞模式,单线程

        ByteBuffer byteBuffer = ByteBuffer.allocate(16);

        // 创建一个服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 服务器绑定端口
        ssc.bind(new InetSocketAddress(9111));

        ssc.configureBlocking(false); // 改为非阻塞模式

        // 连接池
        List<SocketChannel> list = new ArrayList<>();

        // 服务器一直运行
        while (true){
            // System.out.println("进入连接");
            // accept 和客户端进行连接,SocketChannel进行通信
            SocketChannel accept = ssc.accept();    // 阻塞模式,没有连接则会停在这
            if (accept != null){
                System.out.println("连接到:"+accept);
                accept.configureBlocking(false);    //改为非阻塞模式
                list.add(accept);
            }

            for (SocketChannel channel : list) {
                // System.out.println("读取前:"+channel);
                // 读取客户端信息并存入buffer
                int read = channel.read(byteBuffer);// 阻塞模式,没有信息则会停在这
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

    }
}
