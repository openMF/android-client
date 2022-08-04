/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import android.view.Menu
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.collectionsheetindividual.IndividualCollectionSheetFragment
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.PaymentDetailsFragment.OnPayloadSelectedListener
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment
import com.mifos.utils.Constants

class GenerateCollectionSheetActivity : MifosBaseActivity(), OnPayloadSelectedListener {
    var payload: IndividualCollectionSheetPayload? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        intent?.getStringExtra(Constants.COLLECTION_TYPE)?.let { collectionType ->
            if(collectionType == Constants.EXTRA_COLLECTION_INDIVIDUAL)
                replaceFragment(IndividualCollectionSheetFragment.newInstance(),false, R.id.container)
            else if(collectionType == Constants.EXTRA_COLLECTION_COLLECTION)
                replaceFragment(GenerateCollectionSheetFragment.newInstance(),false, R.id.container)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onPayloadSelected(payload: IndividualCollectionSheetPayload?) {
        this.payload = payload
    }
}