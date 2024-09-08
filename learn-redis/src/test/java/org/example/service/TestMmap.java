package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 如果 IO 非常频繁，数据却非常小，推荐使用 mmap，以避免 FileChannel 导致的切态问题。例如索引文件的追加写。
 * MappedByteBuffer 使用是堆外的虚拟内存，因此分配（map）的内存大小不受 JVM 的 -Xmx 参数限制，但是也是有大小限制的。
 * 如果当文件超出 Integer.MAX_VALUE 字节限制时，可以通过 position 参数重新 map 文件后面的内容。
 * MappedByteBuffer 在处理大文件时性能的确很高，但也存内存占用、文件关闭不确定等问题，
 * 被其打开的文件只有在垃圾回收的才会被关闭，而且这个时间点是不确定的。
 */
public class TestMmap {

    private static final int _GB = 1024 * 1024 * 1024;
    private static final int _4kb = 4 * 1024;
    private final static String CONTENT = "Zero copy implemented by MappedByteBuffer";
    private final static String FILE_NAME = "src/test/java/org/example/service/file/mmap.txt";
    private final static String CHARSET = "UTF-8";

    private String file = "src/test/java/org/example/service/file/testmmp.txt";

    @Test
    public void SS() throws IOException {
//        FileChannel fileChannel = new RandomAccessFile(new File("db.data"), "rw").getChannel();
//        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
//        mode：限定内存映射区域（MappedByteBuffer）对内存映像文件的访问模式，包括只可读（READ_ONLY）、可读可写（READ_WRITE）和写时拷贝（PRIVATE）三种模式。
//        position：文件映射的起始地址，对应内存映射区域（MappedByteBuffer）的首地址。
//        size：文件映射的字节长度，从 position 往后的字节数，对应内存映射区域（MappedByteBuffer）的大小。
        MappedByteBuffer map = fileChannel.map(READ_WRITE, 0, _GB);
        for (int i = 0; i < _GB; i++) {
            map.put((byte) 0);
        }

    }

    @Test
    public void writeToFileByMappedByteBuffer() {
        Path path = Paths.get(FILE_NAME);
        byte[] bytes = CONTENT.getBytes(Charset.forName(CHARSET));
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ,
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(READ_WRITE, 0, bytes.length);
            if (mappedByteBuffer != null) {
                mappedByteBuffer.put(bytes);
//                fore()：对于处于 READ_WRITE 模式下的缓冲区，把对缓冲区内容的修改强制刷新到本地文件。
//                load()：将缓冲区的内容载入物理内存中，并返回这个缓冲区的引用。
//                isLoaded()：如果缓冲区的内容在物理内存中，则返回 true，否则返回 false。
                mappedByteBuffer.force();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readFromFileByMappedByteBuffer() {
        Path path = Paths.get(FILE_NAME);
        int length = CONTENT.getBytes(Charset.forName(CHARSET)).length;
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(READ_ONLY, 0, length);
            if (mappedByteBuffer != null) {
                byte[] bytes = new byte[length];
                mappedByteBuffer.get(bytes);
                String content = new String(bytes, StandardCharsets.UTF_8);
                assertEquals(content, "Zero copy implemented by MappedByteBuffer");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testChannel() throws IOException {
        // 方法一: 4kb 刷盘
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
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
        //方法二:单字节刷盘
        FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1);
        byteBuffer.put((byte) 0);
        for (int i = 0; i < _GB; i++) {
            byteBuffer.position(0);
            byteBuffer.limit(1);
            fileChannel.write(byteBuffer);
        }
    }
}
