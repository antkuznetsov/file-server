package server;

import common.Method;

public class Request {
    private Method method;
    private String content;

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
}
