package cn.syl.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel 文件通道
        // 步骤：1.channel通道先获取文件
        // 2.创建缓冲区
        // 3.读取channel数据放在buffer中
        // 4.对buffer进行操作,获取数据
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 缓冲区
            // 10个字节作为缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(3);

            // channel进行读取数据,存放在buffer中
            while (channel.read(buffer) != -1){
                System.out.println("--------------");
                // buffer 切换到读数据模式
                buffer.flip();

                // 查看buffer还有没有数据,有则一直拿
                while (buffer.hasRemaining()){
                    byte b = buffer.get();
                    System.out.println((char) b);
                }

                // buffer 切换到写数据模式, 不切换则会一直重复读
                buffer.compact();
                // byte b = buffer.get();
                // System.out.println((char) b);
            }

        } catch (IOException e) {
        }
    }
}
