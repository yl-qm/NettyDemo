package netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class TestByteBuf {
    public static void main(String[] args) {
        // 默认大小256, 如果超过了这个大小会自动自增,而不是像 ByteBuffer 一样直接报错
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

    }
}
