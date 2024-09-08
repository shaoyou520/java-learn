package org.example.service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel 是一个用于文件读写、映射和操作的通道，同时它在并发环境下是线程安全的，
 * 基于 FileInputStream、FileOutputStream 或者 RandomAccessFile 的 getChannel() 方法可以创建并打开一个文件通道。
 * FileChannel 定义了 transferFrom() 和 transferTo() 两个抽象方法，它通过在通道和通道之间建立连接实现数据传输的。
 */
public class TestFileChannel {

    private static final String CONTENT = "Zero copy implemented by FileChannel";
    private static final String SOURCE_FILE = "source.txt";
    private static final String TARGET_FILE = "target.txt";
    private static final String CHARSET = "UTF-8";

    @Before
    public void setup() {
        Path source = Paths.get(getClassPath(SOURCE_FILE));
        byte[] bytes = CONTENT.getBytes(Charset.forName(CHARSET));
        try (FileChannel fromChannel = FileChannel.open(source, StandardOpenOption.READ,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            fromChannel.write(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getClassPath(String sourceFile) {
        return "src/test/java/org/example/service/file/" + sourceFile;
    }

    @Test
    public void transferTo() throws Exception {
        try (FileChannel fromChannel = new RandomAccessFile(
                getClassPath(SOURCE_FILE), "rw").getChannel();
             FileChannel toChannel = new RandomAccessFile(
                     getClassPath(TARGET_FILE), "rw").getChannel()) {
            long position = 0L;
            long offset = fromChannel.size();
//            以 sendfile 的零拷贝方式尝试数据拷贝
//            如果系统内核不支持 sendfile，进一步执行 transferToTrustedChannel() 方法，以 mmap 的零拷贝方式进行内存映射，
//            这种情况下目的通道必须是 FileChannelImpl 或者 SelChImpl 类型。
//            如果以上两步都失败了，则执行 transferToArbitraryChannel() 方法，基于传统的 I/O 方式完成读写，
//            具体步骤是初始化一个临时的 DirectBuffer，将源通道 FileChannel 的数据读取到 DirectBuffer，
//            再写入目的通道 WritableByteChannel 里面。
            fromChannel.transferTo(position, offset, toChannel);
        }
    }

    @Test
    public void transferFrom() throws Exception {
        try (FileChannel fromChannel = new RandomAccessFile(
                getClassPath(SOURCE_FILE), "rw").getChannel();
             FileChannel toChannel = new RandomAccessFile(
                     getClassPath(TARGET_FILE), "rw").getChannel()) {
            long position = 0L;
            long offset = fromChannel.size();
            toChannel.transferFrom(fromChannel, position, offset);
        }
    }

}
