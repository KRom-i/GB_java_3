package Lesson_01.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Client extends JFrame {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Client () throws IOException {

        socket = new Socket("localhost", 5000);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        setSize(300,300);
        JPanel jPanel = new JPanel(new GridLayout(2,1));

        JButton jButtonSend = new JButton("SEND");
        JTextField jTextField = new JTextField();

        jButtonSend.addActionListener(a -> {

            String[] cmd = jTextField.getText().split(" ");

            if (cmd[0].equals("exit")){
                sendMessage("exit");
                System.exit(0);
            } else if (cmd[0].equals("upload")){
                sendFile(cmd[1]);
            } else if (cmd[0].equals("download")){
                getFile(cmd[1]);
            } else {
                System.out.printf("[%s] not a command\n", cmd);
                jTextField.setText("");
            }

        });

        jPanel.add(jTextField);
        jPanel.add(jButtonSend);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
                sendMessage("exit");
                super.windowClosing(e);
                System.exit(0);
            }
        });
        add(jPanel);
        setVisible(true);
    }

    private void getFile (String fileName) {
        try {
            out.writeUTF("download");
            out.writeUTF(fileName);

            String exists = in.readUTF();

            if (exists.equals("File not found")){
                throw new FileNotFoundException();
            }

            if (exists.equals("download")){
                File file = new File("DIR_JAVA_IO\\client" + File.separator + in.readUTF());

                if (!file.exists()){
                    file.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(file);

                long size = in.readLong();

                byte[] buffer = new byte[8 * 1024];

                int control = (int) (size / buffer.length);
                int control1 = (int) (size + (buffer.length - 1)) / (buffer.length);
                int control2 = (int) (size + (buffer.length)) / (buffer.length);

                for(int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
                    int read = in.read(buffer);
                    fos.write(buffer, 0, read);
                    System.out.printf("%s for time %s\n", i, new Date().toString());
                }

                fos.close();

                out.writeUTF("OK");
                System.out.println("File downloaded");

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendFile (String fileName) {
        try {
            File file = new File("DIR_JAVA_IO\\client" + File.separator + fileName);

            if (!file.exists()){
                throw new FileNotFoundException();
            }

            long fileLength = file.length();
            FileInputStream fis = new FileInputStream(file);

            out.writeUTF("upload");
            out.writeUTF(fileName);
            out.writeLong(fileLength);

            int read = 0;
            byte[] buffer = new byte[8 * 1024];

            while ((read = fis.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }

            out.flush();

            String status = in.readUTF();
            System.out.println("Sending status "  + status);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void sendMessage (String message) {

        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) throws IOException {
        new Client();
    }
}
