package client.utils;

import client.Controller;
import domain.enums.Command;
import javafx.application.Platform;

import java.io.File;

public final class AuthRegExitOfClient {
    private static Controller controller;
    private static String nickname;

    public AuthRegExitOfClient(Controller controller) {
        AuthRegExitOfClient.controller = controller;
    }

    public static void processCommandAuthClient(String command) {
        Platform.runLater(() -> {
            if (command.startsWith(Command.AUTH_OK.getInstruction())) {
                nickname = command.split("\\s")[1];
                controller.setAuthenticated(true);
                controller.getStage().setTitle(String.format("Storage [ %s ]", nickname));
                controller.getNetworkService().sendCommand(Command.OPEN_DIR.getInstruction() + " " +
                        nickname + File.separator);
                controller.getLeftTextArea().appendText(
                        controller.getWithFileWorkable().openDirectoryService(File.separator));
                controller.getLeftTextField().setText(File.separator);
                controller.getRightTextField().setText(nickname + File.separator);
            }
            if (command.startsWith(Command.CHANGE_NICK.getInstruction())) {
                String text = command.replaceFirst(Command.CHANGE_NICK.getInstruction(), "");
                controller.getRegController().resultChangeNick(text);
            }
            if (command.startsWith(Command.NEW_NICK.getInstruction())) {
                String text = command.replaceFirst(Command.NEW_NICK.getInstruction(), "");
                controller.getRegController().resultChangeNick(text);
            }
            if (command.equals(Command.REG_OK.getInstruction())) {
                controller.getRegController().regOk();
            }
            if (command.equals(Command.REG_NO.getInstruction())) {
                controller.getRegController().regNo();
            }
            if (command.equals(Command.END.getInstruction())) {
                controller.getNetworkService().closeConnection();
                throw new RuntimeException("server disconnected us");
            }
            if (command.startsWith(" ")) {
                controller.getTextArea().appendText(command + "\n");
            }
        });
    }

    public static String getNickname() {
        return nickname;
    }
}
