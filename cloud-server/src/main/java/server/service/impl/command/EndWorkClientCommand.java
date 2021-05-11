package server.service.impl.command;


import domain.enums.Command;
import server.service.CommandService;
import server.service.impl.NettyServerService;

public class EndWorkClientCommand implements CommandService {

    @Override
    public String processCommand(String str) {
        NettyServerService.getLogger().info("Client disconnected");
        return Command.END.getInstruction();
    }

    @Override
    public String getCommand() {
        return Command.END.getInstruction();
    }

}
