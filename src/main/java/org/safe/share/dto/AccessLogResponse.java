package org.safe.share.dto;

import java.time.LocalDateTime;

public class AccessLogResponse {
    private String ipAddress;
    private String userAgent;
    private LocalDateTime accessedAt;

    public AccessLogResponse(String ipAddress, String userAgent, LocalDateTime accessedAt) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.accessedAt = accessedAt;
    }

    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public LocalDateTime getAccessedAt() { return accessedAt; }
}
