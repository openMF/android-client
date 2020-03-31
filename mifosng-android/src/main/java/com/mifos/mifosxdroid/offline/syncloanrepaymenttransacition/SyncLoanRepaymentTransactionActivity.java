package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by Rajan Maurya on 30/07/16.
 */
public class SyncLoanRepaymentTransactionActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(SyncLoanRepaymentTransactionFragment.newInstance(), false, R.id.container);
    }
}
