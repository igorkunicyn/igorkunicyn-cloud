package server;

import server.factory.Factory;

public class Main {

    public static void main(String[] args) {
        Factory.getServerService().startServer();
    }
}