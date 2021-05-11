package client.service;

import client.Controller;
import client.service.impl.WorkWithFiles;
import client.utils.AuthRegExitOfClient;
import domain.enums.Command;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MoveAndCopyFile {

    private final Controller controller;

    public MoveAndCopyFile(Controller controller) {
        this.controller = controller;
    }

    public void moveAndCopyFile(String command) {
        String source = controller.getLeftTextField().getText();
        String target = controller.getRightTextField().getText();
        if (target.startsWith(AuthRegExitOfClient.getNickname())) {
            moveAndCopyFileToServer(source, target, command);
        } else if (source.startsWith(AuthRegExitOfClient.getNickname())) {
            moveAndCopyFileFromServer(source, target, command);
        } else {
            moveAndCopyFileOnClient(source, target, command);
        }
    }

    private void moveAndCopyFileToServer(String source, String target, String command) {
        if (!Files.exists(Paths.get(source))) {
            System.out.println("Path is not correct" + " " + source);
            return;
        }
        controller.getNetworkService().sendCommand(WorkWithFiles.createFileForCopyOrMoveToServer
                (source, target));
        if (command.equals("move")) {
            controller.getWithFileWorkable().deleteDirectoryAndFileService(source);
        }
        updateWorkClientWindows(controller.getLeftTextField(), controller.getLeftTextArea(),
                source);
        controller.getNetworkService().sendCommand(Command.OPEN_DIR.getInstruction() + " "
                + Paths.get(target).getParent().toString() + File.separator);

    }

    private void moveAndCopyFileFromServer(String source, String target, String command) {
        if (!Files.isDirectory(Paths.get(target).getParent())) {
            System.out.println("Directory is not exist" + " " + Paths.get(target).getParent());
            return;
        }
        if (command.equals("move")) {
            controller.getNetworkService().sendCommand(Command.MOVE.getInstruction() + " "
                    + source + " " + target);
        } else {
            controller.getNetworkService().sendCommand(Command.COPY.getInstruction() + " "
                    + source + " " + target);
        }
        updateWorkClientWindows(controller.getLeftTextField(), controller.getLeftTextArea(),
                target);
        controller.getNetworkService().sendCommand(Command.OPEN_DIR.getInstruction() + " "
                + Paths.get(source).getParent().toString() + File.separator);

    }

    private void moveAndCopyFileOnClient(String source, String target, String command) {
        if (command.equals("move")) {
            controller.getWithFileWorkable().deleteDirectoryAndFileService(source);
        } else {
            controller.getWithFileWorkable().copyDirectoryAndFileService(source, target);
        }
        updateWorkClientWindows(controller.getLeftTextField(), controller.getLeftTextArea(),
                Paths.get(source).getParent().toString());
        updateWorkClientWindows(controller.getRightTextField(), controller.getRightTextArea(),
                Paths.get(target).getParent().toString());
    }

    public void updateWorkClientWindows(TextField textField, TextArea textArea, String dirPath) {
        textArea.clear();
        String path;
        if (dirPath.contains(".")) {
            path = Paths.get(dirPath).getParent().toString();
        } else {
            path = Paths.get(dirPath).toString();
        }
        textArea.appendText(controller.getWithFileWorkable().openDirectoryService(path));
        textField.setText(path);
    }

}
