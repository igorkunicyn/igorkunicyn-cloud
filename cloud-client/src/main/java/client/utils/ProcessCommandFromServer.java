package client.utils;

import client.Controller;
import client.service.impl.WorkWithFiles;
import domain.FileUpload;
import domain.enums.Command;
import javafx.application.Platform;


public final class ProcessCommandFromServer {
    private static Controller controller;

    public ProcessCommandFromServer(Controller controller) {
        ProcessCommandFromServer.controller = controller;
    }

    public static void updateWorkClientWindowOnServer(FileUpload fileUpload) {
        Platform.runLater(() -> {
            if (fileUpload.getBytes().length > 0) {
                WorkWithFiles.createByteArrayToFile(fileUpload);
            } else if (fileUpload.getNameFile().split(" ")[0].equals(Command.OPEN_DIR.getInstruction())) {
                controller.getRightTextArea().clear();
                controller.getRightTextField().setText(fileUpload.getNameFile().split(" ")[1]);
                controller.getRightTextArea().appendText(fileUpload.getPath());
            } else {
                controller.getNetworkService().sendCommand(Command.OPEN_DIR.getInstruction() + " " +
                        fileUpload.getPath());
            }
        });
    }
}
