package server.service.impl.command;


import domain.enums.Command;
import server.service.CommandService;
import server.service.impl.NettyServerService;


public class AuthClientCommand implements CommandService {


    @Override
    public String processCommand(String str) {
        String[] token = str.split("\\s");
        String newNick = NettyServerService.getInstance().getAuthService()
                .getNicknameByLoginAndPassword(token[1], token[2]);
        if (newNick != null) {
            NettyServerService.getLogger().info("client " + newNick + " connected ");
            return Command.AUTH_OK.getInstruction() + " " + newNick;
        } else {
            return " Неверный логин / пароль";
        }
    }

    @Override
    public String getCommand() {
        return Command.AUTH.getInstruction();
    }
}
