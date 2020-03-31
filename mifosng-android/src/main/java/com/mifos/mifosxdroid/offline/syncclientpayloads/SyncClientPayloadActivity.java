package com.mifos.mifosxdroid.offline.syncclientpayloads;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by Rajan Maurya on 21/07/16.
 */
public class SyncClientPayloadActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(SyncClientPayloadsFragment.newInstance(), false, R.id.container);
    }
}