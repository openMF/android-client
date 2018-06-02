package com.mifos.mifosxdroid.passcode;

import android.content.Intent;
import android.view.View;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.mobile.passcode.MifosPassCodeActivity;
import com.mifos.mobile.passcode.utils.EncryptionUtil;

public class PassCodeActivity extends MifosPassCodeActivity {

    @Override
    public int getLogo() {
        return R.drawable.mifos_logo;
    }

    @Override
    public void startNextActivity() {
        startActivity(new Intent(this, DashboardActivity.class));
    }

    @Override
    public void startLoginActivity() {
        Intent i = new Intent(PassCodeActivity.this, SplashScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void showToaster(View view, int msg) {
        Toaster.show(view, msg);
    }

    @Override
    public int getEncryptionType() {
        return EncryptionUtil.ANDROID_CLIENT;
    }

}
