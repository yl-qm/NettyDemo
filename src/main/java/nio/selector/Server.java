package nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException {
        // 使用selector进行监听
        Selector selector = Selector.open();
        // 创建一个服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 服务器注册进入监听器,并且只关心accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT, null);

        // 服务器绑定端口
        ssc.bind(new InetSocketAddress(9111));

        // 服务器一直运行
        while (true){
            // select方法,没有事件发生线程阻塞,有事件则会运行
            // 如果不处理事件,则selectedKeys方法中一直有数据,所以会一直循环
            // 监听到新的事件后,会把他加入到selectedKeys集合中
            selector.select();
            // selectedKeys 表示需要处理的事件
            // 用完的事件需要进行手动删除,不然下次还会遍历
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();  //一定要删除掉
                System.out.println("SelectionKey: "+key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    System.out.println("accept: "+accept);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                    accept.register(selector, SelectionKey.OP_READ, byteBuffer);    // 把byteBuffer绑定到selector上,只关注读取事件,绑定一个byteBuffer,方便对其进行扩容和数据复制
                }else if (key.isReadable()){    // 正常断开也会触发一次读事件
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer attachment = (ByteBuffer) key.attachment();  // 拿到附件byteBuffer
                        int read = channel.read(attachment);// 如果正常断开,返回值为-1
                        if (read == -1){
                            key.cancel();
                        }else {
                            attachment.flip();
                            while (attachment.hasRemaining()){
                                System.out.println((char) attachment.get());
                            }
                            attachment.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();   // 客户端断开需要取消掉key,不然会一直异常
                    }
                }
            }
        }

    }
}
