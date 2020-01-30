package com.mifos.mifosxdroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

public class AboutActivity extends MifosBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        showBackButton();
    }

    public void goToweb (View view) {
        link("https://github.com/openMF/android-client/graphs/contributors");
    }

    public void goToGit (View view) {
        link("https://github.com/openMF/android-client");
    }

    public void goToTwitter (View view) {
        link("https://twitter.com/mifos");
    }

    public void goToLicense (View view) {
        link("https://github.com/openMF/android-client/blob/master/LICENSE.md");
    }

    public void link(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
