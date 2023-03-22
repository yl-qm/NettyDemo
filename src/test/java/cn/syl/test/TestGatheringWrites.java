package cn.syl.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class TestGatheringWrites {
    public static void main(String[] args) {
        // ByteBuffer encode = StandardCharsets.UTF_8.encode("nihao");
        // String decode = StandardCharsets.UTF_8.decode(encode).toString();
        // System.out.println(decode);

        ByteBuffer encode1 = StandardCharsets.UTF_8.encode("nihao");
        ByteBuffer encode2 = StandardCharsets.UTF_8.encode("2");
        ByteBuffer encode3 = StandardCharsets.UTF_8.encode("3");

        try (FileChannel channel = new RandomAccessFile("33321.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{encode1, encode2, encode3});

        } catch (IOException e) {
        }
    }
}
