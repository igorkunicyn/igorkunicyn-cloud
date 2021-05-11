package server.service;

public interface AuthDBService {

    String getNicknameByLoginAndPassword(String login, String password);

    boolean registration(String login, String password, String nickname);

    boolean changeNick(String oldNickname, String newNickname);
}
