package server;

import common.Method;

public class Request {
    private final Method method;
    private final String content;

    public Request(Method method, String content) {
        this.method = method;
        this.content = content;
    }

    public Method getMethod() {
        return method;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("%s %s", method, content);
    }
}
