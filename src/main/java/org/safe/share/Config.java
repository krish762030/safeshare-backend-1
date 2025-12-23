package org.safe.share;

import org.springframework.beans.factory.annotation.Value;

public class Config {

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public void setFrontendBaseUrl(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }
}
