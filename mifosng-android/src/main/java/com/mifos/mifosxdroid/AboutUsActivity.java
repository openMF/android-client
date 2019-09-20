package com.mifos.mifosxdroid;

import android.os.Bundle;

import com.mifos.mifosxdroid.aboutus.AboutUsFragment;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by Tarun on 23-02-19.
 */

public class AboutUsActivity extends MifosBaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showBackButton();
        replaceFragment(new AboutUsFragment(), false, R.id.container);
    }

}
