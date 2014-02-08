package com.mifos.objects;

import java.util.Arrays;

public class User {

    private String username;
    private int userId;
    private String base64EncodedAuthenticationKey;
    private boolean authenticated;
    private int officeId;
    private String officeName;
    private Role[] roles;
    private Permission[] permissions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBase64EncodedAutheticationKey() {
        return base64EncodedAuthenticationKey;
    }

    public void setBase64EncodedAutheticationKey(String base64EncodedAuthenticationKey) {
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    public Permission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission[] permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                ", base64EncodedAuthenticationKey='" + base64EncodedAuthenticationKey + '\'' +
                ", authenticated=" + authenticated +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", roles=" + Arrays.toString(roles) +
                ", permissions=" + Arrays.toString(permissions) +
                '}';
    }
}
