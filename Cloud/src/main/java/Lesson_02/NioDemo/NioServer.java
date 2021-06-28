package Lesson_02.NioDemo;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class NioServer {

    private static final String HELP_INFO = "\tEnter --help for support info";
    private static final String LS_COMMAND = "\tls     view all files from current directory";
    private static final String CD_COMMAND = "\tcd     changing current directory";
    private static final String MKDIR_COMMAND = "\tmkdir     create directory";
    private static final String TOUCH_COMMAND = "\ttouch      create file";
    private static final String RM_COMMAND = "\trm     delete file / directory";
    private static final String COPY_COMMAND = "\tcopy      copy file / directory";
    private static final String CAT_COMMAND = "\tcat     text file content";
    private static final String CHANGE_NICK_COMMAND = "\tchangenick      change username";

    private static final String[] ALL_COMMAND = {LS_COMMAND, CD_COMMAND, MKDIR_COMMAND, TOUCH_COMMAND,
                                             RM_COMMAND,COPY_COMMAND, CAT_COMMAND, CHANGE_NICK_COMMAND};
    private static final String ROOT = "Server";

    private final ByteBuffer buffer = ByteBuffer.allocate(512);
    private Map<SocketAddress, Client> clients = new HashMap<>();

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
        Client client = getClient(channel);
        clients.put(channel.getRemoteAddress(), client);

        channel.write(ByteBuffer.wrap(String.format("Hello %s !", client.getName()).getBytes(StandardCharsets.UTF_8)));
        channel.write(ByteBuffer.wrap((HELP_INFO + "\n").getBytes(StandardCharsets.UTF_8)));
        sendMessage(client.currentDirInfo(), selector, channel.getRemoteAddress());
    }

    private void handleRead (SelectionKey key, Selector selector) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        SocketAddress clientAddress = channel.getRemoteAddress();
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

        if (key.isValid()) {
            String[] command = sb.toString()
                    .replace("\n", "")
                    .replace("\r", "").split(" ");

            if ("--help".equals(command[0])) {
                for (String cmd : ALL_COMMAND) {
                    sendMessage(cmd, selector, clientAddress);
                }
            } else if ("ls".equals(command[0])) {
                sendMessage(getFilesList(clientAddress).concat("\n"), selector, clientAddress);
            } else if (command.length == 2) {

                if ("touch".equals(command[0])) {
                    sendMessage(createFile(command[1], clientAddress), selector, clientAddress);
                } else if ("mkdir".equals(command[0])) {
                    sendMessage(createDirectory(command[1], clientAddress), selector, clientAddress);
                } else if ("cd".equals(command[0])) {
                    sendMessage(changingCurrentDirectory(command[1], clientAddress), selector, clientAddress);
                } else if ("changenick".equals(command[0])) {
                    clients.get(clientAddress).rename(command[1]);
                } else if ("rm".equals(command[0])) {
                    sendMessage(delete(command[1], clientAddress), selector, clientAddress);
                } else if ("cat".equals(command[0])){
                    for (String str: getTextFile(command[1], clientAddress)) {
                        sendMessage(str, selector, clientAddress);
                    }
                }

            } else if (command.length == 3 && "copy".equals(command[0])){
                sendMessage(copy(command[1], command[2], clientAddress), selector, clientAddress);

            } else if (command[0].length() > 0) {
                sendMessage(HELP_INFO, selector, clientAddress);
            }

            sendMessage(clients.get(clientAddress).currentDirInfo(), selector, clientAddress);
        }
    }

//    При подключении нового клиента создается директория с номером его IP адреса.
//    При дальнейшем подключении данная директория является корневой для клиента.
    private Client getClient(SocketChannel channel) throws IOException {
        String strAddress = channel.getRemoteAddress().toString().substring(1).split(":")[0];
        Path userPath = getUserDir(strAddress);
        if (userPath == null){
            userPath = Path.of(ROOT + File.separator + strAddress);
            Files.createDirectories(userPath);
        }
        return new Client("USER", userPath);
    }

    private Path getUserDir(String strAddress) throws IOException {
        for(File d: new File(ROOT).listFiles()
        ) {
            if (d.isDirectory() && d.getName().equals(strAddress)){
                return d.toPath();
            }
        }
        return null;
    }

    private void sendMessage (String massage, Selector selector, SocketAddress clientAddress) throws IOException {
        for(SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel){
               SocketChannel channel = (SocketChannel) key.channel();
                if (channel.getRemoteAddress().equals(clientAddress)){
                    channel.write(ByteBuffer.wrap((massage + "\n").getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    private String getFilesList (SocketAddress clientAddress) {

        String[] files = new File(clients.get(clientAddress).getCurrentPath().toString()).list();

        if (files.length > 0){
            return String.join(" ", files);
        } else {
            return "The directory is empty";
        }

    }

//    touch (filename) - создание файла
    private String createFile(String fileName, SocketAddress clientAddress) throws IOException {

        Path newFile = Path.of(clients.get(clientAddress).getCurrentPath() +
                File.separator + fileName);

        try {

            if (Files.exists(newFile)){
                return String.format("File [%s] exists", newFile.getFileName());
            }

            Files.createFile(newFile);
            return String.format("File [%s] created", newFile.getFileName());

        } catch (Exception e){
            e.printStackTrace();
            return String.format("Error creating file [%s]", newFile.getFileName());
        }

    }

//    mkdir (dirname) - создание директории
    private String createDirectory (String path, SocketAddress clientAddress) throws IOException {

        Path newDir = Path.of(clients.get(clientAddress).getCurrentPath() +
                               File.separator + path);
        try {

            if (Files.exists(newDir)){
                return String.format("Directory [%s] exists", path);
            }

            Files.createDirectories(newDir);
            return String.format("Directory [%s] created", path);

        } catch (Exception e){
            e.printStackTrace();
            return String.format("Error creating directory [%s]", path);
        }
    }

//    cd (path | ~ | ..) - изменение текущего положения
    private String changingCurrentDirectory(String command, SocketAddress clientAddress){

        Client client = clients.get(clientAddress);

        if ("~".equals(command)){
            client.setCurrentDir("");
        } else  if ("..".equals(command)){
            if (client.getCurrentDir().length() > 0){
                client.stepBack();
            }
        } else {
            if (!Files.exists( Path.of(client.getCurrentPath() +  File.separator + command))){
                return String.format("Directory [%s] not found", command);
            }
            client.setCurrentDir(client.getCurrentDir() + "/" + command);
        }

        return "";
    }

//    rm (filename / dirname) - удаление файла / директории
    private String delete(String command, SocketAddress clientAddress) throws IOException {
        Client client = clients.get(clientAddress);
        Path pathDel = Path.of(client.getCurrentPath() +  File.separator + command);
        if (!Files.exists(pathDel)){
            return String.format("[%s] not found", command);
        }

            try {
                Files.delete(pathDel);
            } catch (DirectoryNotEmptyException e){
                return "Directory not empty.";
            }


        return String.format("[%s] delete", command);
    }

//    copy (src) (target) - копирование файлов / директории
    private String copy(String src, String target, SocketAddress clientAddress) {

        Client client = clients.get(clientAddress);

        Path path1 = Paths.get(client.getCurrentPath() + File.separator + src);
        if (!Files.exists(path1)){
            return String.format("[%s] not found", src);
        }

        Path path2 = Paths.get(client.getCurrentPath() + File.separator + target);

        try {
                Files.copy(path1, path2);
        } catch (IOException e){
            return String.format("Runtime error. Check the specified path [%s]", target);
        }

        return "";
    }

//    cat (filename) - вывод содержимого текстового файла
    private List<String> getTextFile (String file, SocketAddress clientAddress) throws IOException {

        Path pathFile = Path.of(clients.get(clientAddress).getCurrentPath() +
                                File.separator + file);

        List<String> text = new ArrayList<>();

        if (!Files.exists(pathFile)){
            text.add(String.format("[%s]  not found", file));
        } else  if (Files.isDirectory(pathFile)){
            text.add(String.format("[%s]  is not a file", file));
        } else {
            text = Files.readAllLines(pathFile);
        }

        return text;
    }

    public static void main (String[] args) throws Exception {
        new NioServer();
    }

}
