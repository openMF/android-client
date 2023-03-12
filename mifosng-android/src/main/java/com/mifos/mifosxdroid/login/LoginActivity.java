/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.mifos.api.BaseApiManager;
import com.mifos.mifosxdroid.HomeActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.ActivityLoginBinding;
import com.mifos.mifosxdroid.passcode.PassCodeActivity;
import com.mifos.objects.user.User;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;
import com.mifos.utils.ValidationUtil;

import javax.inject.Inject;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends MifosBaseActivity implements LoginMvpView {

    private ActivityLoginBinding binding;

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
            Integer port = binding.etInstancePort
                    .getEditableText().toString().isEmpty() ? null : Integer
                    .valueOf(binding.etInstancePort.getEditableText().toString());
            instanceURL = ValidationUtil
                    .getInstanceUrl(binding.etInstanceURL.getText().toString(), port);
            isValidUrl = ValidationUtil.isValidUrl(instanceURL);
            binding.tvConstructedInstanceUrl.setText(instanceURL);

            domain = binding.etInstanceURL.getEditableText().toString();

            if (domain.length() == 0 || domain.contains(" ")) {
                isValidUrl = false;
            }

            binding.tvConstructedInstanceUrl.setTextColor(isValidUrl ?
                    ContextCompat.getColor(getApplicationContext(), R.color.green_light) :
                    ContextCompat.getColor(getApplicationContext(), R.color.red_light));

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        getActivityComponent().inject(this);
        setContentView(binding.getRoot());

        mLoginPresenter.attachView(this);

        binding.etInstancePort.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (!PrefManager.getPort().equals("80"))
            binding.etInstancePort.setText(PrefManager.getPort());

        binding.etInstanceURL.setText(PrefManager.getInstanceDomain());
        binding.etInstanceURL.addTextChangedListener(urlWatcher);
        binding.etInstancePort.addTextChangedListener(urlWatcher);
        urlWatcher.afterTextChanged(null);

        binding.btLogin.setOnClickListener(view -> onLoginClick());
        binding.etPassword
                .setOnEditorActionListener((textView, i, keyEvent) -> passwordSubmitted(keyEvent));
    }

    public boolean validateUserInputs() {
        domain = binding.etInstanceURL.getEditableText().toString();
        if (domain.length() == 0 || domain.contains(" ")) {
            showToastMessage(getString(R.string.error_invalid_url));
            return false;
        }
        if (!isValidUrl) {
            showToastMessage(getString(R.string.error_invalid_connection));
            return false;
        }
        username = binding.etUsername.getEditableText().toString();
        password = binding.etPassword.getEditableText().toString();
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
                binding.llConnectionSettings.setVisibility(
                        binding.llConnectionSettings.getVisibility() == VISIBLE ? GONE : VISIBLE);


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
            startActivity(new Intent(this, HomeActivity.class));
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
        PrefManager.setTenant(binding.etTenantIdentifier.getEditableText().toString());
        // Saving InstanceURL for next usages
        PrefManager.setInstanceUrl(instanceURL);
        // Saving domain name
        PrefManager.setInstanceDomain(binding.etInstanceURL.getEditableText().toString());
        // Saving port
        PrefManager.setPort(binding.etInstancePort.getEditableText().toString());
        // Updating Services
        BaseApiManager.createService();

        if (Network.isOnline(this)) {
            mLoginPresenter.login(username, password);
        } else {
            showToastMessage(getString(R.string.error_not_connected_internet));
        }
    }
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