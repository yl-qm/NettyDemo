package netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h'});

        // 切片
        // 这个过程不会进行数据复制,也就是说 slice 和 slice1 改变 buffer 内容也会改变
        ByteBuf slice = buffer.slice(0, 4);
        ByteBuf slice1 = buffer.slice(4, 4);
    }
}
