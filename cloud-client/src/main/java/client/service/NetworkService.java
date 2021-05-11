package client.service;


public interface NetworkService {
    void sendCommand(Object o);

    void closeConnection();
}
