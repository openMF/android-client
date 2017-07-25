package com.mifos.objects.twofactor;


public class AccessToken {

    private String token;
    private Long validFrom;
    private Long validTo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Long getValidTo() {
        return validTo;
    }

    public void setValidTo(Long validTo) {
        this.validTo = validTo;
    }
}
