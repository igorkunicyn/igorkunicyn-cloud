package server.service.impl.command;

import domain.FileUpload;
import domain.enums.Command;
import server.handler.SQLHandler;
import server.service.FileProcessable;
import server.service.impl.NettyServerService;

import java.io.File;
import java.nio.file.Paths;

public class CreateDirOnServer implements FileProcessable {

    @Override
    public FileUpload processCommand(String command) {
        String[] strings = command.split("\\s");
        String dirPath = strings[1];
        String nickname = dirPath.split("\\\\")[0];
        String nameFile = Paths.get(dirPath).getFileName().toString() +" "+ "[DIR]";
        String path = Paths.get(dirPath).getParent().toString() + File.separator;
        byte[] bytes = {};
        if (SQLHandler.createDirOnStorage(nickname, nameFile, path, bytes)){
            NettyServerService.getLogger().info("Каталог в хранилище успешно создан");
        }
        return new FileUpload(Command.CREATE.getInstruction(), dirPath, bytes);
    }

    @Override
    public String getCommand() {
        return Command.CREATE.getInstruction();
    }

}
