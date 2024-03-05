/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import android.view.Menu
import androidx.navigation.fragment.NavHostFragment
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityToolbarContainerBinding
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.PaymentDetailsFragment.OnPayloadSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateCollectionSheetActivity : MifosBaseActivity(), OnPayloadSelectedListener {

    private lateinit var binding: ActivityToolbarContainerBinding


    var payload: IndividualCollectionSheetPayload? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolbarContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        showBackButton()
        intent?.getStringExtra(Constants.COLLECTION_TYPE)?.let { collectionType ->
            if (collectionType == Constants.EXTRA_COLLECTION_INDIVIDUAL) {
                navHostFragment.navController.apply {
                    popBackStack()
                    navigate(R.id.individualCollectionSheetFragment)
                }
            } else {
                navHostFragment.navController.apply {
                    popBackStack()
                    navigate(R.id.generateCollectionSheetFragment)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onPayloadSelected(payload: IndividualCollectionSheetPayload?) {
        this.payload = payload
    }
}