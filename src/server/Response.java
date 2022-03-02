package server;

import common.Status;

public class Response {
    private Status status;
    private String content;

    public Response(Status status, String content) {
        this.status = status;
        this.content = content;
    }

    public Response(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        if (content != null) {
            return String.format("%d %s", status.getCode(), content);
        }
        return String.valueOf(status.getCode());
    }
}
