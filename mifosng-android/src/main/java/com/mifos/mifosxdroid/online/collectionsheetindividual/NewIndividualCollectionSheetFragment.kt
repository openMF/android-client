package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.ui.NewIndividualCollectionSheetScreen
import com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.ui.NewIndividualCollectionSheetViewModel
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Created by aksh on 18/6/18.
 */
@AndroidEntryPoint
class NewIndividualCollectionSheetFragment : MifosBaseFragment() {

    private var sheet: IndividualCollectionSheet? = null
    private lateinit var repaymentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            sheet =
                savedInstanceState[Constants.EXTRA_COLLECTION_INDIVIDUAL] as IndividualCollectionSheet
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val viewModel: NewIndividualCollectionSheetViewModel = hiltViewModel()
                val state =
                    viewModel.newIndividualCollectionSheetUiState.collectAsStateWithLifecycle().value

                LaunchedEffect(key1 = state.individualCollectionSheet) {
                    state.individualCollectionSheet?.let {
                        sheet = state.individualCollectionSheet
                        popupDialog()
                    }
                }

                NewIndividualCollectionSheetScreen(state, getStaffList = {
                    viewModel.getStaffList(it)
                },
                    generateCollection = { _officeId, _staffId, _repaymentDate ->
                        viewModel.getIndividualCollectionSheet(RequestCollectionSheetPayload().apply {
                            officeId = _officeId
                            transactionDate = _repaymentDate
                            staffId = _staffId
                        })
                        repaymentDate = _repaymentDate
                    }
                )
            }
        }
    }

    private fun popupDialog() {
        val collectionSheetDialogFragment = CollectionSheetDialogFragment.newInstance(
            repaymentDate,
            sheet?.clients?.size ?: 0
        )
        collectionSheetDialogFragment.setTargetFragment(this, 1)
        val fragmentTransaction = requireActivity().supportFragmentManager
            .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
        collectionSheetDialogFragment.show(fragmentTransaction, "Identifier Dialog Fragment")
    }

    fun getResponse(response: String) {
        when (response) {
            Constants.FILLNOW -> {
                val action = sheet?.let {
                    NewIndividualCollectionSheetFragmentDirections.actionNewIndividualCollectionSheetFragmentToIndividualCollectionSheetDetailsFragment(
                        it, SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(System.currentTimeMillis()), repaymentDate
                    )
                }
                action?.let { findNavController().navigate(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            Constants.EXTRA_COLLECTION_INDIVIDUAL,
            sheet
        )
    }
}