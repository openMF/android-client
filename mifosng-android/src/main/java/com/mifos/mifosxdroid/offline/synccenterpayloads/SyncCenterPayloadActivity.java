package com.mifos.mifosxdroid.offline.synccenterpayloads;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

public class SyncCenterPayloadActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(SyncCenterPayloadsFragment.newInstance(), false, R.id.container);
    }
}