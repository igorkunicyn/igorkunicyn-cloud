package domain.enums;

public enum Command {

    END("/end"), AUTH_OK("/authOk"), AUTH("/auth"),
    DELETE("del"), MOVE("move"), COPY("copy"), CREATE("createDir"),
    OPEN_DIR("openDir"), REG("/reg"), REG_OK("/regOk"), REG_NO("/regNo"),
    CHANGE_NICK("/chNick"), NEW_NICK("/newNick");

    private final String instruction;

    Command(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }
}
