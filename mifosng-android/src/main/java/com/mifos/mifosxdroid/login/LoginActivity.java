/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.App;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.objects.User;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;
import com.mifos.utils.ValidationUtil;

import org.apache.http.HttpStatus;

import javax.net.ssl.SSLHandshakeException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends MifosBaseActivity implements Callback<User>,LoginMvpView {

    @InjectView(R.id.et_instanceURL)
    EditText et_domain;
    @InjectView(R.id.et_username)
    EditText et_username;
    @InjectView(R.id.et_password)
    EditText et_password;
    @InjectView(R.id.tv_constructed_instance_url)
    TextView tv_full_url;
    @InjectView(R.id.bt_connectionSettings)
    TextView bt_connectionSettings;
    @InjectView(R.id.et_tenantIdentifier)
    EditText et_tenantIdentifier;
    @InjectView(R.id.et_instancePort)
    EditText et_port;
    @InjectView(R.id.ll_connectionSettings)
    LinearLayout ll_connectionSettings;

    private String username;
    private String instanceURL;
    private String password;
    private boolean isValidUrl;
    private LoginPresenter mLoginPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        mLoginPresenter.attachView(this);

        et_port.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (!PrefManager.getPort().equals("80"))
            et_port.setText(PrefManager.getPort());

        et_domain.setText(PrefManager.getInstanceDomain());
        bt_connectionSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_connectionSettings.setVisibility(ll_connectionSettings.getVisibility() == VISIBLE ? GONE : VISIBLE);
            }
        });
        et_domain.addTextChangedListener(urlWatcher);
        et_port.addTextChangedListener(urlWatcher);
        urlWatcher.afterTextChanged(null);
    }

    private TextWatcher urlWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Integer port = et_port.getEditableText().toString().isEmpty() ? null : Integer.valueOf(et_port.getEditableText().toString());
            instanceURL = ValidationUtil.getInstanceUrl(et_domain.getText().toString(), port);
            isValidUrl = ValidationUtil.isValidUrl(instanceURL);
            tv_full_url.setText(instanceURL);
            tv_full_url.setTextColor(isValidUrl ? getResources().getColor(R.color.green_light) : getResources().getColor(R.color.red_light));
        }
    };

    public boolean validateUserInputs() {
        if (!isValidUrl) {
            Toaster.show(findViewById(android.R.id.content), "Invalid connection Data");
            return false;
        }
        username = et_username.getEditableText().toString();
        if (username.length() < 5) {
            Toaster.show(findViewById(android.R.id.content), "Invalid username length");
            return false;
        }
        password = et_password.getEditableText().toString();
        if (password.length() < 6) {
            Toaster.show(findViewById(android.R.id.content), "Invalid password length");
            return false;
        }
        return true;
    }

    @Override
    public void success(User user, Response response) {
        hideProgress();
        Toaster.show(findViewById(android.R.id.content), getString(R.string.toast_welcome) + " " + user.getUsername());
        // Saving userID
        PrefManager.setUserId(user.getUserId());
        // Saving InstanceURL for next usages
        PrefManager.setInstanceUrl(instanceURL);
        // Saving domain name
        PrefManager.setInstanceDomain(et_domain.getEditableText().toString());
        // Saving port
        PrefManager.setPort(et_port.getEditableText().toString());
        // Saving tenant
        PrefManager.setTenant(et_tenantIdentifier.getEditableText().toString());
        // Saving user's token
        PrefManager.saveToken("Basic " + user.getBase64EncodedAuthenticationKey());

        startActivity(new Intent(LoginActivity.this, DashboardFragmentActivity.class));
        finish();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        try {
            hideProgress();
            if (retrofitError.getCause() instanceof SSLHandshakeException) {
                promptUserToByPassTheSSLHandshake();
            } else if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                Toaster.show(findViewById(android.R.id.content), getString(R.string.error_login_failed));
            } else if (retrofitError.getResponse().getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                Toaster.show(findViewById(android.R.id.content), "Internal server error");
            }
        } catch (NullPointerException e) {
            if(Network.getConnectivityStatusString(LoginActivity.this).equals("Not connected to Internet") )
            {
                Toaster.show(findViewById(android.R.id.content), "Not connected to Network");
            }
            else {
                Toaster.show(findViewById(android.R.id.content), getString(R.string.error_unknown));
            }
        }
    }

    /**
     * This method should show a dialog box and ask the user
     * if he wants to use and unsafe connection. If he agrees
     * we must update our rest adapter to use an unsafe OkHttpClient
     * that trusts any damn thing.
     */
    private void promptUserToByPassTheSSLHandshake() {
        new AlertDialog.Builder(this)
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
                .create().show();
    }

    @OnClick(R.id.bt_login)
    public void onLoginClick(Button button) {
        login(false);
    }

    private void login(boolean shouldByPassSSLSecurity) {
        if (!validateUserInputs())
            return;

        showProgress("Logging In");
        App.apiManager.setupEndpoint(instanceURL);
        App.apiManager.login(username, password, this);
    }

    @OnEditorAction(R.id.et_password)
    public boolean passwordSubmitted(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            login(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.offline)
            startActivity(new Intent(this, OfflineCenterInputActivity.class));
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccessful(int statuscode) {

        if(statuscode == 200){

        }

    }

    @Override
    public void onLoginError() {

    }

    @Override
    public void onGeneralSignInError() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresenter.detachView();
    }
}