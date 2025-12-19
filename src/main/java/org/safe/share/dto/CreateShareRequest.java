package org.safe.share.dto;

public class CreateShareRequest {
    public Long documentId;
    public int expiryMinutes;
    public String password;
    public boolean oneTime;

}
