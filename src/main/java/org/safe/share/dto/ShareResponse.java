package org.safe.share.dto;

public class ShareResponse {
    private String shareUrl;
    private boolean oneTime;

    public ShareResponse(String shareUrl, boolean oneTime) {
        this.shareUrl = shareUrl;
        this.oneTime = oneTime;
    }

    public String getShareUrl() { return shareUrl; }
    public boolean isOneTime() { return oneTime; }
}

