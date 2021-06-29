package Lesson_03_NETTY.Server;

import Lesson_02.NioDemo.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler extends SimpleChannelInboundHandler<String> {

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

    private Map<ChannelHandlerContext, Client> clients = new HashMap<>();

    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {
        System.out.printf("Client [%s] connected\n", ctx);
        Client client = getClient(ctx);
        clients.put(ctx, client);
        ctx.writeAndFlush(String.format("Hello %s !", client.getName()));
        ctx.writeAndFlush(HELP_INFO + "\n");
        sendMessage(client.currentDirInfo(), ctx);
    }

    @Override
    public void channelInactive (ChannelHandlerContext ctx) throws Exception {
        clients.remove(ctx);
        System.out.printf("Client [%s] disconnected\n", ctx);
    }

    @Override
    protected void channelRead0 (ChannelHandlerContext ctx, String msg) throws Exception {

        String[] command = msg
                .replace("\n", "")
                .replace("\r", "").split(" ");

        if ("--help".equals(command[0])) {
            for (String cmd : ALL_COMMAND) {
                sendMessage(cmd, ctx);
            }
        } else if ("ls".equals(command[0])) {
            sendMessage(getFilesList(ctx).concat("\n"), ctx);
        } else if (command.length == 2) {

            if ("touch".equals(command[0])) {
                sendMessage(createFile(command[1], ctx), ctx);
            } else if ("mkdir".equals(command[0])) {
                sendMessage(createDirectory(command[1], ctx), ctx);
            } else if ("cd".equals(command[0])) {
                sendMessage(changingCurrentDirectory(command[1], ctx), ctx);
            } else if ("changenick".equals(command[0])) {
                clients.get(ctx).rename(command[1]);
            } else if ("rm".equals(command[0])) {
                sendMessage(delete(command[1], ctx),ctx);
            } else if ("cat".equals(command[0])){
                for (String str: getTextFile(command[1], ctx)) {
                    sendMessage(str, ctx);
                }
            }

        } else if (command.length == 3 && "copy".equals(command[0])){
            sendMessage(copy(command[1], command[2], ctx), ctx);

        } else if (command[0].length() > 0) {
            sendMessage(HELP_INFO, ctx);
        }

        sendMessage(clients.get(ctx).currentDirInfo(), ctx);

    }

    private void sendMessage (String massage, ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(massage + "\n");
    }

    //    При подключении нового клиента создается директория с номером его IP адреса.
//    При дальнейшем подключении данная директория является корневой для клиента.
    private Client getClient(ChannelHandlerContext ctx) throws IOException {
        String strAddress = ctx.channel().remoteAddress().toString().substring(1).split(":")[0];
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


    private String getFilesList (ChannelHandlerContext ctx) {

        String[] files = new File(clients.get(ctx).getCurrentPath().toString()).list();

        if (files.length > 0){
            return String.join(" ", files);
        } else {
            return "The directory is empty";
        }

    }

    //    touch (filename) - создание файла
    private String createFile(String fileName, ChannelHandlerContext ctx) throws IOException {

        Path newFile = Path.of(clients.get(ctx).getCurrentPath() +
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
    private String createDirectory (String path, ChannelHandlerContext ctx) throws IOException {

        Path newDir = Path.of(clients.get(ctx).getCurrentPath() +
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
    private String changingCurrentDirectory(String command, ChannelHandlerContext ctx){

        Client client = clients.get(ctx);

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
    private String delete(String command, ChannelHandlerContext ctx) throws IOException {
        Client client = clients.get(ctx);
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
    private String copy(String src, String target, ChannelHandlerContext ctx) {

        Client client = clients.get(ctx);

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
    private List<String> getTextFile (String file, ChannelHandlerContext ctx) throws IOException {

        Path pathFile = Path.of(clients.get(ctx).getCurrentPath() +
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

}
