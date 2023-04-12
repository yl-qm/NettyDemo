package chat.protocol;

import chat.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.io.*;
import java.util.List;

/**
 * 用 Byte 转换成自定义Message
 */
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 魔数,双方约定第一时间来判断消息有没有效果
        out.writeBytes(new byte[]{1,2,3,4});
        // 版本号
        out.writeByte(1);
        // 序列化算法 比如 json , jdk
        out.writeByte(1);
        // 指令类型, 跟业务相关
        out.writeByte(msg.getMessageType());
        // 请求序号
        out.writeInt(msg.getSequenceId());
        // 正文内容转化为 bytes 数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 正文长度
        out.writeInt(bytes.length);
        // 正文内容
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message)ois.readObject();

        System.out.println(message);

    }


}
