package server.service.impl.command;


import domain.enums.Command;
import server.service.CommandService;
import server.service.impl.NettyServerService;

public class RegClientCommand implements CommandService {

    @Override
    public String processCommand(String str) {
        String[] token = str.split("\\s");
        if (token.length == 4) {
            boolean isRegistered = NettyServerService.getInstance().
                    getAuthService().registration(token[1], token[2], token[3]);
            if (isRegistered) {
                return Command.REG_OK.getInstruction();
            } else {
                return Command.REG_NO.getInstruction();
            }
        }
        return Command.REG_NO.getInstruction();
    }

    @Override
    public String getCommand() {
        return Command.REG.getInstruction();
    }

}
