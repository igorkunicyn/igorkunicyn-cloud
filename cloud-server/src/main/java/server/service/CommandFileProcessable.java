package server.service;

import domain.FileUpload;

public interface CommandFileProcessable {

    FileUpload commandProcessFile(String command);

}
