package server.service.impl.command;

import domain.FileUpload;
import domain.enums.Command;
import server.handler.SQLHandler;
import server.service.FileProcessable;
import server.service.impl.NettyServerService;

import java.io.File;
import java.nio.file.Paths;

public class MoveFileFromServer implements FileProcessable {

    @Override
    public FileUpload processCommand(String command) {
        String[] strings = command.split("\\s");
        String out = strings[1];
        String in = strings[2];
        String nickname = out.split("\\\\")[0];
        String nameFile = Paths.get(out).getFileName().toString();
        String path = Paths.get(out).getParent().toString() + File.separator;
        byte[] bytes = SQLHandler.getFileFromStorage(nickname, nameFile, path);
        if (SQLHandler.getDeleteFromStorage(nickname, nameFile, path)) {
            NettyServerService.getLogger().info("Файл удален из хранилища");
        }
        return new FileUpload(nameFile, in, bytes);
    }

    @Override
    public String getCommand() {
        return Command.MOVE.getInstruction();
    }
}
