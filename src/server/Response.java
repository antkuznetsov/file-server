package server;

import common.Status;

public class Response {
    private Status status;
    private Integer fileId;
    private byte[] fileContent;

    public Response(Status status, int fileId) {
        this.status = status;
        this.fileId = fileId;
    }

    public Response(Status status, byte[] fileContent) {
        this.status = status;
        this.fileContent = fileContent;
    }

    public Response(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public int getFileId() {
        return fileId;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        response.append(status.toString());
        if (fileId != null) {
            response.append(' ');
            response.append(fileId);
        }
        if (fileContent != null) {
            response.append(' ');
            response.append(fileContent);
        }
        return response.toString();
    }
}
