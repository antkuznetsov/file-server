package common;

public enum Status {
    OK("200"), FORBIDDEN("403"), NOT_FOUND("404");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return String.valueOf(code);
    }

    public static Status getByCode(String code) {
        for (Status status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}
