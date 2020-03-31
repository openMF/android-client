/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.view.Menu;

import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.collectionsheetindividual.IndividualCollectionSheetFragment;
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.PaymentDetailsFragment;
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment;
import com.mifos.utils.Constants;


public class GenerateCollectionSheetActivity extends MifosBaseActivity
        implements PaymentDetailsFragment.OnPayloadSelectedListener {

    IndividualCollectionSheetPayload payload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        String fragmentToOpen;
        if (getIntent() != null) {
            fragmentToOpen = getIntent().getStringExtra(Constants.COLLECTION_TYPE);
            if (fragmentToOpen.equals(Constants.EXTRA_COLLECTION_INDIVIDUAL)) {
                replaceFragment(IndividualCollectionSheetFragment.newInstance(),
                        false, R.id.container);
            } else if (fragmentToOpen.equals(Constants.EXTRA_COLLECTION_COLLECTION)) {
                replaceFragment(GenerateCollectionSheetFragment.newInstance(),
                        false, R.id.container);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public IndividualCollectionSheetPayload getPayload() {
        return payload;
    }

    @Override
    public void onPayloadSelected(IndividualCollectionSheetPayload payload) {
        this.payload = payload;
    }
}
