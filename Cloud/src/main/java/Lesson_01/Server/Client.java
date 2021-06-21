package Lesson_01.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {

    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public Client () throws IOException {

        socket = new Socket("localhost", 5678);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        setSize(300,300);
        JPanel jPanel = new JPanel(new GridLayout(2,1));

        JButton jButtonSend = new JButton("SEND");
        JTextField jTextField = new JTextField();

        jButtonSend.addActionListener(a -> {
            String message = jTextField.getText();
            sendFile(message);
        });

        jPanel.add(jTextField);
        jPanel.add(jButtonSend);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing (WindowEvent e) {
                super.windowClosing(e);
                sendMessage("exit");
            }
        });
        add(jPanel);
        setVisible(true);
    }

    private void sendFile (String fileName) {
        try {
            File file = new File("client" + File.separator + fileName);

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
