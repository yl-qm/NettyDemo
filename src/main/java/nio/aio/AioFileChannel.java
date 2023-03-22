package nio.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AioFileChannel {
    public static void main(String[] args) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(16);
            /**
             * buffer : ByteBuffer
             * 0 : 起始位置
             * buffer : 附件(如果没有读取完,数据存放位置)
             * CompletionHandler : 回调函数
             */
            fileChannel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    System.out.println(result);
                    attachment.flip();
                    while (attachment.hasRemaining()){
                        System.out.println((char) attachment.get());
                    }
                    attachment.clear();
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
