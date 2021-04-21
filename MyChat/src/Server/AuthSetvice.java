package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthSetvice {

    private static Connection connection;
    private static Statement statement;

    public static void connect(){

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:chat-db.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
    public static boolean addUser(String log, String pass, String nick){

        try {
            String query = "INSERT INTO users (login, password, nickname, history) VALUES (?, ?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, log);
            ps.setInt(2, pass.hashCode());
            ps.setString(3, nick);
            ps.setString(4, "Check in new user");
            ps.executeUpdate();

            String queryBl = "INSERT INTO blacklist (nickname, usersBlackList) VALUES (?, ?);";
            PreparedStatement psBl = connection.prepareStatement(queryBl);
            psBl.setString(1, nick);
            psBl.setString(2, "null");
            psBl.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> blacklistIni(String nickname){

        List<String> blacklist = new ArrayList<>();

        String query = String.format("select usersBlackList from blacklist where nickname='%s'",nickname);

        try {
            ResultSet rs = statement.executeQuery(query);

            if (!rs.getString(1).equals("null")){
                if (rs.next()){
                    String[] tokens = rs.getString(1).split(";");
                    for (String t: tokens) {
                        blacklist.add(t);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return blacklist;
    }

    public static void addUserBlackList(String nickname, List<String> blacklist){

        String bl = "";

        for (int i = 0; i < blacklist.size(); i++) {
            bl += blacklist.get(i) + ";";
        }

        String queryEdit = "UPDATE blacklist SET usersBlackList='"+ bl + "'" +
                " WHERE nickname = '" + nickname + "';";

        try {
            PreparedStatement psEdit = connection.prepareStatement(queryEdit);
            psEdit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkReg(String strfrom, String strVal){
        try {
        String query = String.format("select "+ strfrom +" from users where " + strfrom + "='%s'", strVal);
            ResultSet rs = statement.executeQuery(query);
        if (rs.next()){
            return true;
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static void historyMsgAdd(String nickname, String msg){

        String history = (historyMsg(nickname) + "\n" + msg);

        String queryEdit = "UPDATE users SET history='"+ history + "'" +
                " WHERE nickname = '" + nickname + "';";

        try {

            PreparedStatement psEdit = connection.prepareStatement(queryEdit);
            psEdit.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static String historyMsg(String nickname){

        String query = String.format("select history from users where nickname='%s'",nickname);

        try {
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "No history";
    }

    public static String getNicknameByLoginAndPassword(String login, String password){
        String query = String.format("select nickname, password from users where login='%s'",login);

        try {
            ResultSet rs = statement.executeQuery(query);
            int passHash = password.hashCode();

            if (rs.next()){
                String nick = rs.getString(1);

                int dbHash = rs.getInt(2);

                if (passHash == dbHash){
                    return nick;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
