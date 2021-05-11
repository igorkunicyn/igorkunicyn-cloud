package client;

import client.factory.Factory;
import client.service.*;
import client.service.impl.NettyNetworkService;
import client.utils.AuthRegExitOfClient;
import client.utils.ProcessCommandFromServer;
import domain.enums.Command;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Controller implements Initializable {
    @FXML
    private HBox authPanel;
    @FXML
    private HBox buttonPanel;
    @FXML
    private VBox vBox;
    @FXML
    private MenuBar menuBarPanel;
    @FXML
    private SplitPane splitPanel;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField leftTextField;
    @FXML
    private TextArea leftTextArea;
    @FXML
    private TextArea rightTextArea;
    @FXML
    private TextField rightTextField;
    private TextField currentTextField;
    private TextArea currentTextArea;
    public Socket socket;
    private Stage stage;
    private Stage regStage;
    private RegController regController;
    private NettyNetworkService networkService;
    private WithFileWorkable withFileWorkable;
    public AuthRegExitOfClient authRegExitOfClient;
    public MoveAndCopyFile moveAndCopyFile;
    public ProcessCommandFromServer processCommandFromServer;

    public Controller() {
        this.authRegExitOfClient = new AuthRegExitOfClient(this);
        this.moveAndCopyFile = new MoveAndCopyFile(this);
        this.processCommandFromServer = new ProcessCommandFromServer(this);
    }

    public void setAuthenticated(boolean authenticated) {
        buttonPanel.setVisible(authenticated);
        buttonPanel.setManaged(authenticated);
        splitPanel.setVisible(authenticated);
        splitPanel.setManaged(authenticated);
        menuBarPanel.setVisible(authenticated);
        menuBarPanel.setManaged(authenticated);
        textArea.setVisible(!authenticated);
        textArea.setManaged(!authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        textArea.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService = Factory.getNetworkService();
        withFileWorkable = Factory.getWithFileWorkable();
        Platform.runLater(() -> stage = ( Stage ) vBox.getScene().getWindow());
        setAuthenticated(false);
    }

    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        String msg = String.format("%s %s %s",
                Command.AUTH.getInstruction(), loginField.getText().trim(), passwordField.getText().trim());
        networkService.sendCommand(msg);
    }

    @FXML
    public void tryToReg(String login, String password, String nickname) {
        if (socket == null || socket.isClosed()) {
            networkService = Factory.getNetworkService();
        }
        String msg = String.format("%s %s %s %s",
                Command.REG.getInstruction(), login, password, nickname);
        networkService.sendCommand(msg);
    }

    @FXML
    public void registration(ActionEvent actionEvent) {
        if (regStage == null) {
            createRegWindow();
        }
        regStage.show();
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("Storage registration");
            regStage.setScene(new Scene(root, 400, 350));
            regController = fxmlLoader.getController();
            regController.setController(this);
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void copyFileUseButton(ActionEvent actionEvent) {
        String command = "copy";
        moveAndCopyFile.moveAndCopyFile(command);
    }

    @FXML
    public void moveFileUseButton(ActionEvent actionEvent) {
        String command = "move";
        moveAndCopyFile.moveAndCopyFile(command);
    }

    @FXML
    public void deleteFileAndDirectoryUseButton(ActionEvent actionEvent) {
        currentTextField = determineWorkedTextField();
        currentTextArea = determineWorkedTextArea(currentTextField);
        String commandText = currentTextField.getText();
        if (commandText.startsWith(AuthRegExitOfClient.getNickname())) {
            networkService.sendCommand(Command.DELETE.getInstruction() + " " + commandText);
        } else {
            withFileWorkable.deleteDirectoryAndFileService(commandText);
            moveAndCopyFile.updateWorkClientWindows(currentTextField, currentTextArea, commandText);
        }
    }

    private TextField determineWorkedTextField() {
        if (rightTextField.getText().equals("")) {
            return leftTextField;
        }
        return rightTextField;
    }

    private TextArea determineWorkedTextArea(TextField textField) {
        if (textField.equals(leftTextField)) {
            return leftTextArea;
        }
        return rightTextArea;
    }

    @FXML
    public void createFileUseButton(ActionEvent actionEvent) {
        currentTextField = determineWorkedTextField();
        currentTextArea = determineWorkedTextArea(currentTextField);
        String commandText = currentTextField.getText();
        if (commandText.startsWith(AuthRegExitOfClient.getNickname())) {
            networkService.sendCommand(Command.CREATE.getInstruction() + " " + commandText);
        } else {
            withFileWorkable.createDirectoryAndFileService(commandText);
            moveAndCopyFile.updateWorkClientWindows(currentTextField, currentTextArea, commandText);
        }
    }

    @FXML
    public void openDirectoryUseButton(ActionEvent actionEvent) {
        currentTextField = determineWorkedTextField();
        currentTextArea = determineWorkedTextArea(currentTextField);
        String commandText = currentTextField.getText();
        if (commandText.startsWith(AuthRegExitOfClient.getNickname())) {
            networkService.sendCommand(Command.OPEN_DIR.getInstruction() + " " + commandText);
        } else {
            moveAndCopyFile.updateWorkClientWindows(currentTextField, currentTextArea, commandText);

        }
    }

    public void shutdown() {
        networkService.sendCommand(Command.END.getInstruction());
        networkService.closeConnection();
    }

    public NettyNetworkService getNetworkService() {
        return networkService;
    }

    public WithFileWorkable getWithFileWorkable() {
        return withFileWorkable;
    }

    public RegController getRegController() {
        return regController;
    }

    public TextField getRightTextField() {
        return rightTextField;
    }

    public TextArea getLeftTextArea() {
        return leftTextArea;
    }

    public TextArea getRightTextArea() {
        return rightTextArea;
    }

    public TextField getLeftTextField() {
        return leftTextField;
    }

    public Stage getStage() {
        return stage;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}


