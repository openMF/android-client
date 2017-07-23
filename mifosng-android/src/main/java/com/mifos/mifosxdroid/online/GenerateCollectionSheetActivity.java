/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.view.Menu;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.collectionsheetindividual.IndividualCollectionSheetFragment;
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment;
import com.mifos.utils.Constants;


public class GenerateCollectionSheetActivity extends MifosBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
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
}
