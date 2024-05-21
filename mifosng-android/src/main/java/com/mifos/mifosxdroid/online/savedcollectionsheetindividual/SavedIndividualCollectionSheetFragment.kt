package com.mifos.mifosxdroid.online.savedcollectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.feature.individual_collection_sheet.saved_individual_collection_sheet.ui.SavedIndividualCollectionSheetCompose
import com.mifos.mifosxdroid.core.MifosBaseFragment

/**
 * Created by aksh on 18/6/18.
 */
class SavedIndividualCollectionSheetFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavedIndividualCollectionSheetCompose()
            }
        }
    }
}