/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mifos.mifosxdroid.base.BaseActivity;
import com.mifos.mifosxdroid.databinding.LoginBinding;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.objects.User;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.ValidationUtils;

import org.apache.http.HttpStatus;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.SSLHandshakeException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.mifos.utils.Constants.API_PATH;
import static com.mifos.utils.Constants.PROTOCOL_HTTPS;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends BaseActivity implements Callback<User> {
    private Integer port = null;
    private String username;
    private String instanceURL;
    private String password;

    private API api;
    private LoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        String previouslyEnteredUrl = MifosApplication.spManager.getInstanceUrl();
        String previouslyEnteredPort = MifosApplication.spManager.getInstancePort();

        if (!previouslyEnteredUrl.startsWith("http"))
            previouslyEnteredUrl = Constants.PROTOCOL_HTTPS + previouslyEnteredUrl;

        binding.etInstanceURL.setText(previouslyEnteredUrl);

        if (!previouslyEnteredPort.equals("80"))
            binding.etInstancePort.setText(previouslyEnteredPort);

        updateMyInstanceUrl();

        binding.btConnectionSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.llConnectionSettings.getVisibility() == VISIBLE)
                    binding.llConnectionSettings.setVisibility(GONE);
                else
                    binding.llConnectionSettings.setVisibility(VISIBLE);
            }
        });

        binding.btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.passwordWrapper.setErrorEnabled(false);
                binding.userWrapper.setErrorEnabled(false);
                binding.urlWrapper.setErrorEnabled(false);

                username = binding.etUsername.getEditableText().toString();
                if (username.length() < 5) {
                    binding.userWrapper.setError("Username is not valid");
                    return;
                }
                password = binding.etPassword.getEditableText().toString();
                if (password.length() < 6) {
                    binding.passwordWrapper.setError("Password is not valid");
                    return;
                }

                instanceURL = binding.etInstanceURL.getEditableText().toString();
                if (!TextUtils.isEmpty(binding.etInstancePort.getEditableText()))
                    port = Integer.parseInt(binding.etInstancePort.getEditableText().toString());


                String domain = ValidationUtils.sanitizeDomainNameInput(instanceURL);

                if (!ValidationUtils.validateURL(domain)) {
                    binding.urlWrapper.setError("Domain name is not valid");
                    return;
                }

                try {
                    String constructedURL = constructInstanceUrl(domain, port);
                    URL url = new URL(constructedURL);
                    instanceURL = url.toURI().toString();
                } catch (MalformedURLException e) {
                    binding.urlWrapper.setError("Url is not valid");
                    return;
                } catch (URISyntaxException uriException) {
                    binding.urlWrapper.setError("Url is not valid");
                    return;
                }

                login(false);
            }
        });
        binding.etInstanceURL.addTextChangedListener(urlWatcher);
        binding.etInstancePort.addTextChangedListener(urlWatcher);
    }

    private TextWatcher urlWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateMyInstanceUrl();
        }
    };

    private void login(boolean shouldByPassSSLSecurity) {
        showProgress("Logging in");

        api = new API(instanceURL, binding.etTenantIdentifier.getEditableText().toString().trim(), shouldByPassSSLSecurity);
        api.userAuthService.authenticate(username, password, this);
    }

    private void updateMyInstanceUrl() {
        String textUnderConstruction;

        if (!binding.etInstancePort.getEditableText().toString().isEmpty())
            port = Integer.valueOf(binding.etInstancePort.getEditableText().toString().trim());

        instanceURL = binding.etInstanceURL.getEditableText().toString();
        String domain = ValidationUtils.sanitizeDomainNameInput(instanceURL);
        textUnderConstruction = constructInstanceUrl(domain, port);

        binding.tvConstructedInstanceUrl.setText(textUnderConstruction);
    }

    public String constructInstanceUrl(String validDomain, Integer port) {
        return PROTOCOL_HTTPS + validDomain + (port != null ? (":" + port) : "") + API_PATH;
    }

    @Override
    public void success(User user, Response response) {
        MifosApplication.api = api;
        hideProgress();

        Snackbar.make(binding.getRoot(), getString(R.string.toast_welcome) + " " + user.getUsername(), Snackbar.LENGTH_SHORT).show();

        MifosApplication.spManager.setInstanceUrl(ValidationUtils.sanitizeDomainNameInput(binding.etInstanceURL.getEditableText().toString()));
        MifosApplication.spManager.setInstanceDomain(ValidationUtils.sanitizeDomainNameInput(binding.etInstanceURL.getEditableText().toString()));

        if (port != null)
            MifosApplication.spManager.setInstancePort(port.toString());

        String tenant = binding.etTenantIdentifier.getEditableText().toString();
        if (TextUtils.isEmpty(tenant))
            tenant = "default";

        MifosApplication.spManager.setTenantId(tenant);
        MifosApplication.spManager.setAuthToken("Basic " + user.getBase64EncodedAuthenticationKey());
        startActivity(new Intent(LoginActivity.this, DashboardFragmentActivity.class));
        finish();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        try {
            hideProgress();
            if (retrofitError.getCause() instanceof SSLHandshakeException) {
                promptUserToByPassTheSSLHandshake();
            } else if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED)
                Snackbar.make(binding.getRoot(), R.string.error_login_failed, Snackbar.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            Snackbar.make(binding.getRoot(), R.string.error_unknown, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * This method should show a dialog box and ask the user
     * if he wants to use and unsafe connection. If he agrees
     * we must update our rest adapter to use an unsafe OkHttpClient
     * that trusts any damn thing.
     */
    private void promptUserToByPassTheSSLHandshake() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("SSL Certificate Problem")
                .setMessage("There is a problem with your SSLCertificate, would you like to continue? This connection would be unsafe.")
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        login(true);
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.offline:
                startActivity(new Intent(this, OfflineCenterInputActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}