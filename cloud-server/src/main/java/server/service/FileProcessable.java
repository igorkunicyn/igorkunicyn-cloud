package server.service;

import domain.FileUpload;

public interface FileProcessable {

    FileUpload processCommand(String command);

    String getCommand();
}
