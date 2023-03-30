/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.user;

import androidx.annotation.NonNull;

import org.apache.fineract.client.models.PostAuthenticationResponse;
import org.apache.fineract.client.models.RoleData;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static final String AUTHENTICATION_KEY = "authenticationKey";

    private String username;
    private long userId;
    private String base64EncodedAuthenticationKey;
    private boolean authenticated;
    private long officeId;
    private String officeName;
    private List<RoleData> roles = new ArrayList<RoleData>();
    private List<String> permissions = new ArrayList<String>();

    //{"username":"User1","userId":1,"base64EncodedAuthenticationKey":"VXNlcjE6dGVjaDRtZg\u003d
    // \u003d",
// "authenticated":true,"officeId":1,"officeName":"Office1",
// "roles":[{"id":1,"name":"Admin","description":"Admin"}],
// "permissions":["ALL_FUNCTIONS"],"shouldRenewPassword":false}
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(long officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public List<RoleData> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleData> roles) {
        this.roles = roles;
    }

    public String getBase64EncodedAuthenticationKey() {
        return base64EncodedAuthenticationKey;
    }

    public void setBase64EncodedAuthenticationKey(String base64EncodedAuthenticationKey) {
        this.base64EncodedAuthenticationKey = base64EncodedAuthenticationKey;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
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
                ", roles=" + roles +
                ", permissions=" + permissions +
                '}';
    }

    public User setUserFromPostAuthRes(@NonNull PostAuthenticationResponse response) {
        username = response.getUsername();
        userId = response.getUserId();
        base64EncodedAuthenticationKey = response.getBase64EncodedAuthenticationKey();
        authenticated = response.getAuthenticated();
        officeId = response.getOfficeId();
        officeName = response.getOfficeName();
        roles = response.getRoles();
        permissions = response.getPermissions();
        return this;
    }
}
