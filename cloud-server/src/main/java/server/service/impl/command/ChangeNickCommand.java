package server.service.impl.command;

import domain.enums.Command;
import server.handler.SQLHandler;
import server.service.CommandService;

public class ChangeNickCommand implements CommandService {

    @Override
    public String processCommand(String command) {
        String[] token = command.split("\\s");
        if (token.length < 4) {
            return Command.CHANGE_NICK.getInstruction() + "Не хватает данных для смены имени";
        }
        if (token[3].contains(" ")) {
            return Command.CHANGE_NICK.getInstruction() + "Ник не может содержать пробелов";
        }
        if (SQLHandler.changeNick(SQLHandler.getNicknameByLoginAndPassword(token[1], token[2]), token[3])) {
            return (Command.NEW_NICK.getInstruction() + "Ваш ник изменен на " + token[3]);
        } else {
            return Command.CHANGE_NICK.getInstruction() + "Не удалось изменить ник. Ник " + token[3] + " уже существует";
        }
    }

    @Override
    public String getCommand() {
        return Command.CHANGE_NICK.getInstruction();
    }

}
