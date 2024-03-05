package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetDetailsAdapter
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetDetailsAdapter.ListAdapterListener
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.IndividualCollectionsSheetDetailsBinding
import com.mifos.mifosxdroid.online.GenerateCollectionSheetActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by aksh on 20/6/18.
 */
@AndroidEntryPoint
class IndividualCollectionSheetDetailsFragment : MifosBaseFragment(), OnRetrieveSheetItemData,
    ListAdapterListener {

    private lateinit var binding: IndividualCollectionsSheetDetailsBinding
    private val arg: IndividualCollectionSheetDetailsFragmentArgs by navArgs()

    private lateinit var viewModel: IndividualCollectionSheetDetailsViewModel

    var sheetsAdapter: IndividualCollectionSheetDetailsAdapter? = null
    private var sheet: IndividualCollectionSheet? = null
    private var paymentTypeList: List<String>? = null
    private var loansAndClientNames: List<LoanAndClientName> = emptyList()
    var payload: IndividualCollectionSheetPayload? = null
    private val requestCode = 1
    private var actualDisbursementDate: String? = null
    private var transactionDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            sheet =
                savedInstanceState[Constants.EXTRA_COLLECTION_INDIVIDUAL] as IndividualCollectionSheet
            showCollectionSheetViews(sheet)
        }
        sheet = arg.sheet
        actualDisbursementDate = arg.actualDisbursementDate
        transactionDate = arg.transactionDate
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IndividualCollectionsSheetDetailsBinding.inflate(inflater, container, false)
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        sheetsAdapter = IndividualCollectionSheetDetailsAdapter(requireContext(), this)
        viewModel = ViewModelProvider(this)[IndividualCollectionSheetDetailsViewModel::class.java]
        payload = (activity as GenerateCollectionSheetActivity).payload
        showCollectionSheetViews(sheet)

        viewModel.individualCollectionSheetDetailsUiState.observe(viewLifecycleOwner) {
            when (it) {
                is IndividualCollectionSheetDetailsUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is IndividualCollectionSheetDetailsUiState.ShowProgressbar -> showProgressbar(true)
                is IndividualCollectionSheetDetailsUiState.ShowSuccess -> {
                    showProgressbar(false)
                    showSuccess()
                }
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_individual_collectionsheet, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_submit_sheet -> {
                submitSheet()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCollectionSheetViews(sheet: IndividualCollectionSheet?) {
        paymentTypeList = viewModel.filterPaymentTypeOptions(sheet?.paymentTypeOptions)
        loansAndClientNames = viewModel.filterLoanAndClientNames(sheet?.clients)

        //Initialize payload's BulkRepaymentTransactions array with default values.
        //The changes made (if any) will be updated by the interface 'OnRetrieveSheetItemData'

        //methods.
        if (payload == null) {
            payload = IndividualCollectionSheetPayload()
            for (loanAndClientName in viewModel.filterLoanAndClientNames(sheet?.clients)) {
                val loanCollectionSheet = loanAndClientName.loan
                if (loanCollectionSheet != null) {
                    payload?.bulkRepaymentTransactions?.add(
                        BulkRepaymentTransactions(
                            loanCollectionSheet.loanId,
                            loanCollectionSheet.totalDue +
                                    loanCollectionSheet.chargesDue
                        )
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerCollections.layoutManager = layoutManager
        binding.recyclerCollections.adapter = sheetsAdapter
        sheetsAdapter?.setSheetItemClickListener(this)
        sheetsAdapter?.setLoans(loansAndClientNames)
        sheetsAdapter?.setPaymentTypeList(paymentTypeList)
        sheetsAdapter?.setPaymentTypeOptionsList(sheet?.paymentTypeOptions)
        sheetsAdapter?.notifyDataSetChanged()
    }

    private fun showSuccess() {
        Toaster.show(binding.root, getStringMessage(R.string.collectionsheet_submit_success))
    }

    private fun showError(error: String?) {
        Toaster.show(binding.root, error)
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions, position: Int) {}
    override fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions, position: Int) {
        payload!!.bulkRepaymentTransactions[position] = transaction
    }

    override fun listItemPosition(position: Int) {
        val paymentTypeOptionList: Array<String>? = paymentTypeList?.toTypedArray()
        val paymentTypeOptions = sheet?.paymentTypeOptions?.toTypedArray()
        val current = loansAndClientNames[position]
        val clientId = current.id
        val action = payload?.let {
            paymentTypeOptionList?.let { it1 ->
                paymentTypeOptions?.let { it2 ->
                    IndividualCollectionSheetDetailsFragmentDirections.actionIndividualCollectionSheetDetailsFragmentToPaymentDetailsFragment(
                        position,
                        it,
                        it1,
                        current,
                        it2,
                        clientId
                    )
                }
            }
        }
        action?.let { findNavController().navigate(it) }
    }

    private fun submitSheet() {
        if (payload == null) {
            Toaster.show(binding.root, getStringMessage(R.string.error_generate_sheet_first))
        } else {
            payload?.actualDisbursementDate = actualDisbursementDate
            payload?.transactionDate = transactionDate
            viewModel.submitIndividualCollectionSheet(payload)
        }
    }
}