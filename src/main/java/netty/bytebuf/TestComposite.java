package netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

public class TestComposite {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h'});

        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        buffer1.writeBytes(new byte[]{'1','2','3','4','5','6','7','8'});

        // 不用内存复制,但是要把读写指针重新计算
        CompositeByteBuf compositeBuffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeBuffer.addComponents(true, buffer, buffer1);

        // 这个方法底层调用 composite ,和上面效果一样
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer, buffer1);

    }
}
