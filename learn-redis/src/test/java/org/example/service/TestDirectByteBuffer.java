package org.example.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * DirectByteBuffer 内部的字节缓冲区位在于堆外的（用户态）直接内存，
 * 它是通过 Unsafe 的本地方法 allocateMemory() 进行内存分配，底层调用的是操作系统的 malloc() 函数。
 * 除此之外，初始化 DirectByteBuffer 时还会创建一个 Deallocator 线程，并通过 Cleaner 的 freeMemory()
 * 方法来对直接内存进行回收操作，freeMemory() 底层调用的是操作系统的 free() 函数。

 */
public class TestDirectByteBuffer {
    private static final int _GB = 1024 * 1024 * 1024;
    private static final int _4kb = 4 * 1024;
    private String file1 = "src/test/java/org/example/service/file/data2.txt";
    private String file2 = "src/test/java/org/example/service/file/data1.txt";

    @Test
    void testChannel() throws IOException {
        // 方法一: 4kb 刷盘
        FileChannel fileChannel = new RandomAccessFile(file1, "rw").getChannel();
//        DirectByteBuffer 内部的字节缓冲区位在于堆外的（用户态）直接内存，它是通过 Unsafe 的本地方法 allocateMemory() 进行内存分配，底层调用的是操作系统的 malloc() 函数。
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_4kb);
        for (int i = 0; i < _4kb; i++) {
            byteBuffer.put((byte) 0);
        }
        for (int i = 0; i < _GB; i += _4kb) {
            byteBuffer.position(0);
            byteBuffer.limit(_4kb);
            fileChannel.write(byteBuffer);
        }

    }

    @Test
    void testFileChannel() throws IOException {
        //方法二:单字节刷盘， 时间很长
        FileChannel fileChannel = new RandomAccessFile(file2, "rw").getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1);
        byteBuffer.put((byte) 0);
        for (int i = 0; i < _GB; i++) {
            byteBuffer.position(0);
            byteBuffer.limit(1);
            fileChannel.write(byteBuffer);
        }
    }
}
