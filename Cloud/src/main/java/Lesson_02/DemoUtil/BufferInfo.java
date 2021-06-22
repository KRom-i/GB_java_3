package Lesson_02.DemoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class BufferInfo {

    public static void main (String[] args) throws IOException {
        /*

        position = 0;                   capacity = 6;
        _______________________________
        |    |    |    |    |    |    |
        _______________________________
                                          limit = 6;
        position
        limit
        capacity
        mark


        position = 3;                   capacity = 6;
        _______________________________
        |  X |  X |  X |    |    |    |
        _______________________________
                                       limit = 6;
        read (poi = 0; lim = 3)
         */


        FileChannel channel = new RandomAccessFile("Client" + File.separator + "1.txt", "rw").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        channel.read(buffer);
        buffer.flip(); //

        System.out.println(buffer);
//        while (buffer.hasRemaining()){
//            System.out.print((char) buffer.get());
//        }
//        System.out.println("\n" + buffer);

        byte[] bytesBuffer = new byte[10];
        int pos = 0;
        while (buffer.hasRemaining()){
            bytesBuffer[pos++] = buffer.get();
        }
        System.out.println(new String(bytesBuffer, StandardCharsets.UTF_8));

        buffer.rewind();
    }
}
