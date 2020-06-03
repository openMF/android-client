/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mifos.api.BaseApiManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.mifosxdroid.passcode.PassCodeActivity;
import com.mifos.objects.user.User;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;
import com.mifos.utils.ValidationUtil;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends MifosBaseActivity implements LoginMvpView {

    @BindView(R.id.et_instanceURL)
    EditText et_domain;

    @BindView(R.id.et_username)
    EditText et_username;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.tv_constructed_instance_url)
    TextView tv_full_url;

    @BindView(R.id.et_tenantIdentifier)
    EditText et_tenantIdentifier;

    @BindView(R.id.et_instancePort)
    EditText et_port;

    @BindView(R.id.ll_connectionSettings)
    LinearLayout ll_connectionSettings;

    @Inject
    LoginPresenter mLoginPresenter;

    private String username;
    private String instanceURL;
    private String password;
    private String domain;
    private boolean isValidUrl = false;

    private TextWatcher urlWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Integer port = et_port.getEditableText().toString().isEmpty() ? null : Integer
                    .valueOf(et_port.getEditableText().toString());
            instanceURL = ValidationUtil.getInstanceUrl(et_domain.getText().toString(), port);
            isValidUrl = ValidationUtil.isValidUrl(instanceURL);
            tv_full_url.setText(instanceURL);

            domain = et_domain.getEditableText().toString();

            if (domain.length() == 0 || domain.contains(" ")) {
                isValidUrl = false;
            }

            tv_full_url.setTextColor(isValidUrl ?
                    ContextCompat.getColor(getApplicationContext(), R.color.green_light) :
                    ContextCompat.getColor(getApplicationContext(), R.color.red_light));

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        mLoginPresenter.attachView(this);

        et_port.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (!PrefManager.getPort().equals("80"))
            et_port.setText(PrefManager.getPort());

        et_domain.setText(PrefManager.getInstanceDomain());
        et_domain.addTextChangedListener(urlWatcher);
        et_port.addTextChangedListener(urlWatcher);
        urlWatcher.afterTextChanged(null);
    }

    public boolean validateUserInputs() {
        domain = et_domain.getEditableText().toString();
        if (domain.length() == 0 || domain.contains(" ")) {
            showToastMessage(getString(R.string.error_invalid_url));
            return false;
        }
        if (!isValidUrl) {
            showToastMessage(getString(R.string.error_invalid_connection));
            return false;
        }
        username = et_username.getEditableText().toString();
        password = et_password.getEditableText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            showToastMessage(getString(R.string.error_enter_credentials));
            return false;
        } else {
            if (username.length() < 5) {
                showToastMessage(getString(R.string.error_username_length));
                return false;
            }
            if (password.length() < 6) {
                showToastMessage(getString(R.string.error_password_length));
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mItem_connection_settings:
                ll_connectionSettings.setVisibility(
                        ll_connectionSettings.getVisibility() == VISIBLE ? GONE : VISIBLE);


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void showToastMessage(String message) {
        Toaster.show(findViewById(android.R.id.content), message, Toaster.INDEFINITE);
    }

    @Override
    public void onLoginSuccessful(User user) {
        // Saving userID
        PrefManager.setUserId(user.getUserId());
        // Saving user's token
        PrefManager.saveToken("Basic " + user.getBase64EncodedAuthenticationKey());
        // Saving user
        PrefManager.saveUser(user);

        Toast.makeText(this, getString(R.string.toast_welcome) + " " + user.getUsername(),
                Toast.LENGTH_SHORT).show();

        if (PrefManager.getPassCodeStatus()) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            Intent intent = new Intent(this, PassCodeActivity.class);
            intent.putExtra(Constants.INTIAL_LOGIN, true);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onLoginError(String errorMessage) {
        showToastMessage(errorMessage);
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show) {
            showProgress(getString(R.string.logging_in));
        } else {
            hideProgress();
        }
    }

    @OnClick(R.id.bt_login)
    public void onLoginClick() {
        //Hide the keyboard, when user clicks on login button
        hideKeyboard(findViewById(R.id.bt_login));
        login();
    }

    private void login() {
        if (!validateUserInputs()) {
            return;
        }
        // Saving tenant
        PrefManager.setTenant(et_tenantIdentifier.getEditableText().toString());
        // Saving InstanceURL for next usages
        PrefManager.setInstanceUrl(instanceURL);
        // Saving domain name
        PrefManager.setInstanceDomain(et_domain.getEditableText().toString());
        // Saving port
        PrefManager.setPort(et_port.getEditableText().toString());
        // Updating Services
        BaseApiManager.createService();

        if (Network.isOnline(this)) {
            mLoginPresenter.login(username, password);
        } else {
            showToastMessage(getString(R.string.error_not_connected_internet));
        }
    }

    @OnEditorAction(R.id.et_password)
    public boolean passwordSubmitted(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            login();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresenter.detachView();
    }
}