package Lesson_02.NioDemo;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NioServer {

    private static final String LS_COMMAND = "\tls     view all files from current directory";
    private static final String MKDIR_COMMAND = "\tmkdir     view all files from current directory";
    private static final String[] ALL_COMMAND = {LS_COMMAND, MKDIR_COMMAND};
    private static final String ROOT = "Server";

    private final ByteBuffer buffer = ByteBuffer.allocate(512);
    private Map<SocketAddress, String> clients = new HashMap<>();

    public NioServer () throws Exception{

        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(6666));
        server.configureBlocking(false);
        Selector selector = Selector.open();

        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.printf("Server [%s] start\n", getClass().getName());
        while (server.isOpen()){
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                    handleAccept(key, selector);
                } else if(key.isReadable()){
                    handleRead(key, selector);
                }
                iterator.remove();
            }
        }
    }



    private void handleAccept (SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        System.out.printf("Client IP [%s] connected\n", channel.getRemoteAddress());
        channel.register(selector, SelectionKey.OP_READ);
        channel.write(ByteBuffer.wrap("Hello user !\t".getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap("Enter --help for support info".getBytes(StandardCharsets.UTF_8)));
    }

    private void handleRead (SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        SocketAddress client = channel.getRemoteAddress();
        int readBytes = channel.read(buffer);

        if (readBytes < 0){
            channel.close();
            return;
        } else if (readBytes == 0){
            return;
        }

        buffer.flip();
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()){
            sb.append((char) buffer.get());
        }
        buffer.clear();

        if (key.isValid()){
            String command = sb.toString()
                    .replace("\n", "")
                    .replace("\r", "");
            if ("--help".equals(command)){
                for(String cmd: ALL_COMMAND) {
                    sendMessage(cmd, selector, client);
                }
            } else if ("ls".equals(command)){
                sendMessage(getFilesList().concat("\n"), selector, client);
            }
        }
    }

    private void sendMessage (String massage, Selector selector, SocketAddress client) throws IOException {
        for(SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel){
               SocketChannel channel = (SocketChannel) key.channel();
                if (channel.getRemoteAddress().equals(client)){
                    channel.write(ByteBuffer.wrap((massage + "\n").getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    private String getFilesList () {
        return String.join(" ", new File(ROOT).list());
    }

    public static void main (String[] args) throws Exception {
        new NioServer();
    }

}
