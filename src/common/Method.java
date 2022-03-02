package common;

public enum Method {
    GET, PUT, DELETE, EXIT;

    public static Method getByName(String name) {
        for (Method method : values()) {
            if (method.toString().equals(name)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Method is not supported!");
    }
}
