package client;

public enum Action {
    GET_FILE("1"), CREATE_FILE("2"), DELETE_FILE("3"), EXIT("exit");

    private final String code;

    Action(String code) {
        this.code = code;
    }

    public static Action getByCode(String code) {
        for (Action accessType : values()) {
            if (accessType.code.equals(code)) {
                return accessType;
            }
        }
        throw new IllegalArgumentException();
    }
}
