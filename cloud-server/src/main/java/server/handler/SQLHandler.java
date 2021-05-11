package server.handler;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNickname;
    private static PreparedStatement psRegistration;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psAddFiles;
    private static PreparedStatement psGetListDirAndFileFromStorage;
    private static PreparedStatement psGetFileFromStorage;
    private static PreparedStatement psDeleteFileFromStorage;
    private static PreparedStatement psCreateDirOnStorage;

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloudstorage.db");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS client" +
                    " ( client_id INTEGER PRIMARY KEY AUTOINCREMENT , login char(50) NOT NULL UNIQUE, password char(50) NOT NULL UNIQUE," +
                    "nickname char(50) NOT NULL UNIQUE);").execute();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS storage" +
                    " ( storage_id INTEGER PRIMARY KEY AUTOINCREMENT , client_id INTEGER NOT NULL," +
                    "name_file char(50) , path char(100) NOT NULL, file BLOB, FOREIGN KEY (client_id)" +
                    " REFERENCES client(client_id));").execute();
            prepareAllStatements();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void prepareAllStatements() throws SQLException {
        psGetNickname = connection.prepareStatement("SELECT nickname FROM client WHERE login = ? AND password = ?;");
        psRegistration = connection.prepareStatement("INSERT INTO client(login, password, nickname) VALUES (? ,? ,? );");
        psChangeNick = connection.prepareStatement("UPDATE client SET nickname = ? WHERE nickname = ?;");
        psAddFiles = connection.prepareStatement("INSERT INTO storage (client_id, name_file, path, file) VALUES (\n" +
                "(SELECT client_id FROM client WHERE nickname=?),\n" +
                "?, ?, ?)");
        psGetListDirAndFileFromStorage = connection.prepareStatement("SELECT name_file FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) AND path = ? ORDER BY name_file");
        psGetFileFromStorage = connection.prepareStatement("SELECT file FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) " +
                "AND name_file = ? AND path = ?");
        psDeleteFileFromStorage = connection.prepareStatement("DELETE FROM storage \n" +
                "WHERE client_id = (SELECT client_id FROM client WHERE nickname=?) " +
                "AND name_file = ? AND path = ?");
        psCreateDirOnStorage = connection.prepareStatement("INSERT INTO storage (client_id, name_file, path, file) VALUES (\n" +
                "(SELECT client_id FROM client WHERE nickname=?),\n" +
                "?, ?, ?)");
    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            psGetNickname.setString(1, login);
            psGetNickname.setString(2, password);
            ResultSet rs = psGetNickname.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }

    public static boolean registration(String login, String password, String nickname) {
        try {
            psRegistration.setString(1, login);
            psRegistration.setString(2, password);
            psRegistration.setString(3, nickname);
            psRegistration.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeNick(String oldNickname, String newNickname) {
        try {
            psChangeNick.setString(1, newNickname);
            psChangeNick.setString(2, oldNickname);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // добавляет в базу данных файлы и сведения о их местоположении
    public static boolean addFiles(String nickname, String name_file, String path, byte[] file) {
        try {
            psAddFiles.setString(1, nickname);
            psAddFiles.setString(2, name_file);
            psAddFiles.setString(3, path);
            psAddFiles.setBytes(4, file);
            psAddFiles.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // получаем список файлов из директории
    public static String getListDirAndFileFromStorage(String nickname, String path) {
        StringBuilder sb = new StringBuilder();
        try {
            psGetListDirAndFileFromStorage.setString(1, nickname);
            psGetListDirAndFileFromStorage.setString(2, path);
            ResultSet rs = psGetListDirAndFileFromStorage.executeQuery();
            while (rs.next()) {
                String string = rs.getString(1);
                sb.append(String.format("%s\n", string));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // получаем файл из базы данных
    public static byte[] getFileFromStorage(String nickname, String name_file, String path) {
        byte[] bytes = {};
        try {
            psGetFileFromStorage.setString(1, nickname);
            psGetFileFromStorage.setString(2, name_file);
            psGetFileFromStorage.setString(3, path);
            ResultSet rs = psGetFileFromStorage.executeQuery();
            if (rs.next()) {
                bytes = rs.getBytes(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    // удаляем файл из базы данных
    public static boolean getDeleteFromStorage(String nickname, String name_file, String path) {
        try {
            psDeleteFileFromStorage.setString(1, nickname);
            psDeleteFileFromStorage.setString(2, name_file);
            psDeleteFileFromStorage.setString(3, path);
            psDeleteFileFromStorage.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //  создаем директорию в  базе данных
    public static boolean createDirOnStorage(String nickname, String name_file, String path, byte[] bytes) {
        try {
            psCreateDirOnStorage.setString(1, nickname);
            psCreateDirOnStorage.setString(2, name_file);
            psCreateDirOnStorage.setString(3, path);
            psCreateDirOnStorage.setBytes(4, bytes);
            psCreateDirOnStorage.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            psRegistration.close();
            psGetNickname.close();
            psChangeNick.close();
            psAddFiles.close();
            psGetListDirAndFileFromStorage.close();
            psGetFileFromStorage.close();
            psDeleteFileFromStorage.close();
            psCreateDirOnStorage.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
