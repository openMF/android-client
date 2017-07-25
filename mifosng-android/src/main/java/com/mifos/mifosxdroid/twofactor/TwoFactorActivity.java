package com.mifos.mifosxdroid.twofactor;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.passcode.PassCodeActivity;
import com.mifos.objects.twofactor.AccessToken;
import com.mifos.objects.twofactor.DeliveryMethod;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


public class TwoFactorActivity extends MifosBaseActivity implements TwoFactorMvpView {


    @BindView(R.id.tfa_delivery)
    LinearLayout ll_delivery;

    @BindView(R.id.tfa_otp)
    LinearLayout ll_token;

    @BindView(R.id.tfa_deliveryMethodsList)
    RadioGroup rd_deliveryMethods;

    @BindView(R.id.bt_tfa_request_otp)
    Button bt_request;

    @BindView(R.id.bt_tfa_login)
    Button bt_login;

    @BindView(R.id.et_tfa_token)
    EditText et_token;

    @Inject
    TwoFactorPresenter twoFactorPresenter;

    private Integer selectedDeliveryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_two_factor);
        ButterKnife.bind(this);
        twoFactorPresenter.attachView(this);

        twoFactorPresenter.fetchDeliveryMethods();

        rd_deliveryMethods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedDeliveryId = checkedId;
                bt_request.setEnabled(true);
            }
        });
    }

    private void sendOTPRequest() {
        if (selectedDeliveryId != null) {
            twoFactorPresenter.requestOTP(selectedDeliveryId);
        }
    }

    private void validateToken() {
        String token = et_token.getEditableText().toString();
        if (token.isEmpty()) {
            showToast(getString(R.string.tfa_invalid_token_length));
            return;
        }

        twoFactorPresenter.validateToken(token);
    }

    @Override
    public void onOTPRequested() {
        ll_delivery.setVisibility(View.GONE);
        ll_token.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeliveryMethodsFetch(List<DeliveryMethod> deliveryMethods) {
        rd_deliveryMethods.removeAllViews();
        rd_deliveryMethods.clearCheck();
        for (int i = 0; i < deliveryMethods.size(); i++) {
            final DeliveryMethod deliveryMethod = deliveryMethods.get(i);
            final RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i + 1);
            if (deliveryMethod.getName().equals("sms")) {
                radioButton.setText(getString(R.string.tfa_sms_text, deliveryMethod.getTarget()));
            } else if (deliveryMethod.getName().equals("email")) {
                radioButton.setText(getString(R.string.tfa_email_text, deliveryMethod.getTarget()));
            } else {
                radioButton.setText(getString(R.string.tfa_unknown_text, deliveryMethod.getName(),
                        deliveryMethod.getTarget()));
            }
            radioButton.setLayoutParams(new
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            rd_deliveryMethods.addView(radioButton);
        }
    }

    @Override
    public void onOTPTokenValidated(AccessToken accessToken) {
        String username = PrefManager.getUser().getUsername();
        PrefManager.saveTwoFactorToken(accessToken.getToken());
        PrefManager.saveTwoFactorTokenExpiryTime(accessToken.getValidTo());
        Toast.makeText(this, getString(R.string.toast_welcome) + " " + username,
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PassCodeActivity.class);
        intent.putExtra(Constants.INTIAL_LOGIN, true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.bt_tfa_request_otp)
    public void onRequestClick() {
        sendOTPRequest();
    }

    @OnClick(R.id.bt_tfa_login)
    public void onLoginClick() {
        validateToken();
    }

    @OnEditorAction(R.id.et_tfa_token)
    public boolean tokenSubmitted(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            validateToken();
            return true;
        }
        return false;
    }

    @Override
    public void showLoadingProgressbar() {
        showProgressbar(true, getString(R.string.loading));
    }

    @Override
    public void showProgressbar(boolean show) {
        showProgressbar(show, getString(R.string.logging_in));
    }

    private void showProgressbar(boolean show, String text) {
        if (show) {
            showProgress(text);
        } else {
            hideProgress();
        }
    }

    @Override
    public void showDeliveryFetchErrorToast() {
        showToast(getString(R.string.tfa_delivery_fetch_error));
    }

    @Override
    public void showOTPRequestErrorToast() {
        showToast(getString(R.string.tfa_otp_request_error));
    }

    @Override
    public void showWrongTokenToast() {
        showToast(getString(R.string.tfa_invalid_token));
    }

    private void showToast(String message) {
        Toaster.show(findViewById(android.R.id.content), message, Toaster.INDEFINITE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        twoFactorPresenter.detachView();
    }

}
