package com.mifos.mifosxdroid;

import android.os.Bundle;

import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by mayankjindal on 22/07/17.
 */

public class SettingsActivity extends MifosBaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();
    }
}
