package server.service.impl.command;

import domain.FileUpload;
import domain.enums.Command;
import server.handler.SQLHandler;
import server.service.FileProcessable;

import java.io.File;
import java.nio.file.Paths;

public class CopyFileFromServer implements FileProcessable {

    @Override
    public FileUpload processCommand(String command) {
        String[] strings = command.split("\\s");
        String source = strings[1];
        String target = strings[2];
        String nickname = source.split("\\\\")[0];
        String nameFile = Paths.get(source).getFileName().toString();
        String path = Paths.get(source).getParent().toString() + File.separator;
        byte[] bytes = SQLHandler.getFileFromStorage(nickname, nameFile, path);
        return new FileUpload(nameFile, target, bytes);
    }

    @Override
    public String getCommand() {
        return Command.COPY.getInstruction();
    }
}
