package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by Rajan Maurya on 20/08/16.
 */
public class SyncSavingsAccountTransactionActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(SyncSavingsAccountTransactionFragment.newInstance(), false, R.id.container);
    }
}
