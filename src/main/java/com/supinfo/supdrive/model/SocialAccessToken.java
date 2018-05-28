package com.supinfo.supdrive.model;

import javax.validation.constraints.NotBlank;

public class SocialAccessToken {

    @NotBlank
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
