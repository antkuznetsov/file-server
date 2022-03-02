package common;

public enum Status {
    OK(200), FORBIDDEN(403), NOT_FOUND(404);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public static Status getByCode(int code) {
        for (Status status : values()) {
            if (code == status.code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status code is not valid!");
    }

    public int getCode() {
        return code;
    }
}
