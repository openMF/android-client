package com.mifos.mifosxdroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

public class HelpActivity extends MifosBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_help);
        showBackButton();
    }

    public void goToIssue(View view) {
        link("https://github.com/openMF/android-client/issues/new");
    }

    public void goToContact(View view) {
        link("https://mifos.org/about-us/contact-us/");
    }

    public void goToDonate(View view) {
        link("https://mifos.org/take-action/donate-now/");
    }

    public void goToTerms (View view) {
        link("https://openmf.github.io/privacy_policy_mifos_mobile.html");
    }

    public void link(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}