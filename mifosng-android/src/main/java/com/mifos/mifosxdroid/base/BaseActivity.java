package com.mifos.mifosxdroid.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.utils.MifosApplication;

/**
 * @author fomenkoo
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected Handler handler;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showProgress(String message) {
        if (progress == null)
            progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);

        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }

    public void hideProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void logout() {
        MifosApplication.spManager.setAuthToken("NA");
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }
}
