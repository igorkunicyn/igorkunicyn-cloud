package server.service.impl.command;

import domain.FileUpload;
import domain.enums.Command;
import server.handler.SQLHandler;
import server.service.FileProcessable;

public class OpenDirOnServer implements FileProcessable {

    @Override
    public FileUpload processCommand(String command) {
        String[] strings = command.split("\\s");
        String nickname = strings[1].split("\\\\")[0];
        byte[] bytes = {};
        String listOpenDir = SQLHandler.getListDirAndFileFromStorage(nickname, strings[1]);
        return new FileUpload(Command.OPEN_DIR.getInstruction() + " " + strings[1], listOpenDir, bytes);
    }

    @Override
    public String getCommand() {
        return Command.OPEN_DIR.getInstruction();
    }

}
