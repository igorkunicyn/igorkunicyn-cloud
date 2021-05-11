package client;

import domain.enums.Command;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    public TextField loginField;
    public PasswordField passwordField;
    public TextField nicknameField;
    public TextArea textArea;
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();
        controller.tryToReg(login, password, nickname);
    }

    public void regOk() {
        textArea.appendText("Регистрация прошла успешно\n");
    }

    public void regNo() {
        textArea.appendText("Логин или имя уже заняты\n");
    }

    public void resultChangeNick(String text) {
        textArea.appendText(text + System.lineSeparator());
    }

    @FXML
    public void changeNick(ActionEvent actionEvent) {
        String msg = String.format("%s %s %s %s",
                Command.CHANGE_NICK.getInstruction(), loginField.getText().trim(),
                passwordField.getText().trim(), nicknameField.getText().trim());
        controller.getNetworkService().sendCommand(msg);
    }

}
