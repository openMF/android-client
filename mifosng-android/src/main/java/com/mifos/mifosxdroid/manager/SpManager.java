package com.mifos.mifosxdroid.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.User;
import com.mifos.utils.CachedValue;
import com.mifos.utils.Constants;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fomenkoo
 */
public class SpManager implements Manager {

    private SharedPreferences sp;
    private Set<CachedValue> cachedValues;


    private CachedValue<String> instanceUrl;
    private CachedValue<String> instancePort;
    private CachedValue<String> instanceDomain;
    private CachedValue<String> authToken;
    private CachedValue<String> tenantId;

    @Override
    public void init(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        CachedValue.initialize(sp);
        cachedValues = new HashSet<>();

        cachedValues.add(instanceUrl = new CachedValue<>(Constants.INSTANCE_URL_KEY, context.getString(R.string.default_instance_url), String.class));
        cachedValues.add(instanceDomain = new CachedValue<>(Constants.INSTANCE_DOMAIN_KEY, context.getString(R.string.default_instance_url), String.class));
        cachedValues.add(instancePort = new CachedValue<>(Constants.INSTANCE_PORT_KEY, "80", String.class));
        cachedValues.add(authToken = new CachedValue<>(User.AUTHENTICATION_KEY, "NA", String.class));
        cachedValues.add(tenantId = new CachedValue<>(Constants.TENANT_IDENTIFIER_KEY, null, String.class));

    }

    public String getInstanceUrl() {
        return instanceUrl.getValue();
    }

    public String getInstancePort() {
        return instancePort.getValue();
    }

    public String getAuthToken() {
        return authToken.getValue();
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl.setValue(instanceUrl);
    }

    public String getInstanceDomain() {
        return instanceDomain.getValue();
    }

    public void setInstanceDomain(String instanceDomain) {
        this.instanceDomain.setValue(instanceDomain);
    }

    public void setInstancePort(String instancePort) {
        this.instancePort.setValue(instancePort);
    }

    public void setAuthToken(String authToken) {
        this.authToken.setValue(authToken);
    }

    public String getTenantId() {
        return tenantId.getValue();
    }

    public void setTenantId(String tenantId) {
        this.tenantId.setValue(tenantId);
    }

    @Override
    public void clear() {
        for (CachedValue value : cachedValues) {
            value.delete();
        }
    }
}
