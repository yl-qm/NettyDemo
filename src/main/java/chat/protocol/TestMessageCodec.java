package chat.protocol;

import chat.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 11, 4, 0, 0),
                new LoggingHandler(),
                new MessageCodec()
        );

        Message message = new Message() {
            @Override
            public int getMessageType() {
                return 0;
            }

            @Override
            public String toString() {
                return "good";
            }
        };

        // encode
        channel.writeOutbound(message);

        // decode
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, byteBuf);

        channel.writeInbound(byteBuf);

    }

}
