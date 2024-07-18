package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.feature.individual_collection_sheet.individual_collection_sheet.ui.IndividualCollectionSheetScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Tarun on 05-07-2017.
 */
class IndividualCollectionSheetFragment : MifosBaseFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        toolbar?.visibility = View.GONE
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                IndividualCollectionSheetScreen(onBackPressed = {
                    requireActivity().onBackPressed()
                }, onDetail = { repaymentDate, individualCollectionSheet ->
                    getIndividualCollectionSheetDetails(repaymentDate, individualCollectionSheet)
                })
            }
        }
    }

    private fun getIndividualCollectionSheetDetails(
        repaymentDate: String,
        individualCollectionSheet: IndividualCollectionSheet
    ) {
        val action =
            IndividualCollectionSheetFragmentDirections.actionIndividualCollectionSheetFragmentToIndividualCollectionSheetDetailsFragment(
                individualCollectionSheet, SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale.getDefault()
                ).format(System.currentTimeMillis()), repaymentDate
            )
        findNavController().navigate(action)
    }
}