package client;

public enum AccessType {
    BY_NAME("1"), BY_ID("2");

    private final String code;

    AccessType(String code) {
        this.code = code;
    }

    public static AccessType getByCode(String code) {
        for (AccessType accessType : values()) {
            if (accessType.code.equals(code)) {
                return accessType;
            }
        }
        throw new IllegalArgumentException();
    }
}
