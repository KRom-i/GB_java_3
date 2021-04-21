package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientHandler {

    private ServerChat server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private List<String> blacklist;
    private boolean checkAuth;
    private boolean isExit;


    public ClientHandler(ServerChat server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blacklist = new LinkedList<>();

            new Thread(() ->{

                isExit = false;
                checkAuth = false;
                try {
                    while (true){

                        String str = in.readUTF();

                        if(str.startsWith("/auth ")) {
                            String[] tokens = str.split(" ");
                            String nick = AuthSetvice.getNicknameByLoginAndPassword(
                                    tokens[1], tokens[2]);
                            if (nick != null && !server.checkNick(nick)) {
                                checkAuth = true;
                                sendMSG("/auth-OK " + nick);
                                sendMSG(AuthSetvice.historyMsg(nick));
                                setNickname(nick);
                                server.subscribe(ClientHandler.this);
                                blacklist = AuthSetvice.blacklistIni(getNickname());
                                System.out.println(blacklist.size());
                                break;
                            } else {
                                sendMSG("Wrong Login/password");
                            }

                        } else if (str.startsWith("/signup ")) {
                                String[] tokensReg = str.split(" ");

                                if (AuthSetvice.checkReg("login", tokensReg[1])){
                                    sendMSG("login failed");
                                } else if (AuthSetvice.checkReg("nickname", tokensReg[3])){
                                    sendMSG("nickname failed");

                                } else if (AuthSetvice.addUser(tokensReg[1], tokensReg[2], tokensReg[3])) {
                                    sendMSG("Successful registration");
                                    System.out.printf("Successful registration log [%s], pass [%s], nick [%s]",
                                            tokensReg[1], tokensReg[2].hashCode(), tokensReg[3]);
                                } else {
                                    sendMSG("Registration failed");
                                    System.out.printf("Registration failed log [%s], pass [%s], nick [%s]",
                                            tokensReg[1], tokensReg[2].hashCode(), tokensReg[3]);
                                }

                            }

                        if(str.equals("/end")){
                            out.writeUTF("/serverClosed");
                            System.out.printf("Client [%s] disconnected \n", socket.getInetAddress());
                            isExit = true;
                            break;
                        }


                    }

                    if (!isExit) {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/") || str.startsWith("@")) {

                                if (str.equals("/end")) {
                                    out.writeUTF("/serverClosed");
                                    System.out.printf("Client [%s] disconnected \n", socket.getInetAddress());
                                    break;

                                } else if (str.startsWith("@")) {
                                    String[] tokensMSG = str.split(" ", 2);
                                    String nickOut = tokensMSG[0].substring(1, tokensMSG[0].length());

                                    if (!checkBlackList(nickOut)) {
                                        if (server.checkNick(nickOut)) {
                                            server.privateMSG(getNickname(), nickOut, tokensMSG[1]);
                                        } else {
                                            sendMSG("ERR! Nick [" + nickOut + "] not found");
                                        }
                                    } else {
                                        sendMSG("Nick [" + nickOut + "] you blacklist");
                                    }


                                } else if (str.startsWith("/blacklist")) {
                                    String[] nickBlackList = str.split(" ");
                                    blacklist.add(nickBlackList[1]);
                                    AuthSetvice.addUserBlackList(getNickname(), blacklist);
                                    sendMSG("You added [" + nickBlackList[1] + "] to blacklist");
                                } else if (str.startsWith("/clientlist")) {
                                    server.broadcastContactsList();
                                }

                            } else {
                                server.broadcastMSG(this, nickname + ": " + str);
                            }
                        }
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                server.unsubscribe(this);

            }).start();

            new Thread(() ->{
                try {
                    Thread.sleep(120000);
                    if (!checkAuth){
                        try {
                            out.writeUTF("/endTimeAuth");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setNickname(String nick) {
        this.nickname = nick;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMSG (String str){
     try {
         out.writeUTF(str);
     } catch (IOException e) {
         e.printStackTrace();
     }
 }

    public boolean checkBlackList(String nickname) {
        return blacklist.contains(nickname);
    }
}
