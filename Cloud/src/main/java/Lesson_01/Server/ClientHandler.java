package Lesson_01.Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;

    public ClientHandler (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run () {

        try (
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())
        ){

            System.out.printf("Client %s connected\n", socket.getInetAddress());
            while (true){

                String command = in.readUTF();

                if ("upload".equals(command)){

                    try {
                        File file = new File("server" + File.separator + in.readUTF());

                        if (!file.exists()){
                            file.createNewFile();
                        }

                        FileOutputStream fos = new FileOutputStream(file);

                        long size = in.readLong();

                        byte[] buffer = new byte[8 * 1024];

                        for(int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
                            int read = in.read(buffer);
                            fos.write(buffer, 0, read);
                        }
                        fos.close();

                        out.writeUTF("OK");
                        System.out.printf("Client [%s] upload file [%s]\n", socket.getInetAddress(), file.getName());

                    } catch (Exception e){
                        out.writeUTF("FATAL ERROR !");
                        e.printStackTrace();
                    }

                } else if ("download".equals(command)){

                    try {

                        File file = new File("server" + File.separator + in.readUTF());

                        if (file.exists()){

                            long fileLength = file.length();
                            FileInputStream fis = new FileInputStream(file);

                            out.writeUTF("download");
                            out.writeUTF(file.getName());
                            out.writeLong(fileLength);


                            int read = 0;
                            byte[] buffer = new byte[8 * 1024];

                            while ((read = fis.read(buffer)) != -1){
                                out.write(buffer, 0, read);
                            }

                            out.flush();

                            String status = in.readUTF();

                            if (status.equals("OK")){
                                System.out.printf("Client [%s] downloaded the file [%s]\n", socket.getInetAddress(), file.getName());
                            }

                        } else {
                            out.writeUTF("File not found");
                        }

                    } catch (Exception e){
                        out.writeUTF("FATAL ERROR !");
                        e.printStackTrace();
                    }

                } else if ("exit".equals(command)){

                    System.out.printf("Client %s disconnected\n", socket.getInetAddress());
                    break;

                } else {
                    System.out.println(command);
                    out.writeUTF(command);
                }


            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
