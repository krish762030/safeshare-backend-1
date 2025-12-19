package org.safe.share.dto;

public class DocumentDownload {

    private byte[] data;
    private String fileName;
    private String contentType;

    public DocumentDownload(byte[] data, String fileName, String contentType) {
        this.data = data;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }
}
