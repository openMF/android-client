package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetDetailsAdapter
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetDetailsAdapter.ListAdapterListener
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.GenerateCollectionSheetActivity
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.collectionsheet.LoanAndClientName
import com.mifos.utils.Constants
import java.util.*
import javax.inject.Inject

/**
 * Created by aksh on 20/6/18.
 */
class IndividualCollectionSheetDetailsFragment : MifosBaseFragment(), IndividualCollectionSheetDetailsMvpView, OnRetrieveSheetItemData, ListAdapterListener {
    @JvmField
    @BindView(R.id.recycler_collections)
    var recyclerSheets: RecyclerView? = null

    @JvmField
    @Inject
    var presenter: IndividualCollectionSheetDetailsPresenter? = null
    var sheetsAdapter: IndividualCollectionSheetDetailsAdapter? = null
    private var sheet: IndividualCollectionSheet? = null
    private var paymentTypeList: List<String?>? = null
    private var loansAndClientNames: List<LoanAndClientName?>? = null
    var payload: IndividualCollectionSheetPayload? = null
        private set
    private lateinit var rootView: View
    private val requestCode = 1
    private var actualDisbursementDate: String? = null
    private var transactionDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (savedInstanceState != null) {
            sheet = savedInstanceState[Constants.EXTRA_COLLECTION_INDIVIDUAL] as IndividualCollectionSheet
            showCollectionSheetViews(sheet)
        }
        sheet = requireArguments().getParcelable(Constants.INDIVIDUAL_SHEET)
        actualDisbursementDate = requireArguments().getString(Constants.DISBURSEMENT_DATE)
        transactionDate = requireArguments().getString(Constants.TRANSACTION_DATE)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.individual_collections_sheet_details,
                container, false)
        ButterKnife.bind(this, rootView)
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        sheetsAdapter = IndividualCollectionSheetDetailsAdapter(context, this)
        presenter!!.attachView(this)
        payload = (activity as GenerateCollectionSheetActivity?)!!.payload
        showCollectionSheetViews(sheet)
        return rootView
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
        paymentTypeList = presenter!!.filterPaymentTypeOptions(sheet!!.paymentTypeOptions)
        loansAndClientNames = presenter!!.filterLoanAndClientNames(sheet.clients)

        //Initialize payload's BulkRepaymentTransactions array with default values.
        //The changes made (if any) will be updated by the interface 'OnRetrieveSheetItemData'

        //methods.
        if (payload == null) {
            payload = IndividualCollectionSheetPayload()
            for (loanAndClientName in presenter!!.filterLoanAndClientNames(sheet.clients)) {
                val loanCollectionSheet = loanAndClientName!!.loan
                payload!!.getBulkRepaymentTransactions().add(BulkRepaymentTransactions(
                        loanCollectionSheet.loanId,
                        if (loanCollectionSheet.chargesDue == null) loanCollectionSheet.totalDue else loanCollectionSheet.totalDue +
                                loanCollectionSheet.chargesDue))
            }
        }
        val layoutManager = LinearLayoutManager(context)
        recyclerSheets!!.layoutManager = layoutManager
        recyclerSheets!!.adapter = sheetsAdapter
        sheetsAdapter!!.setSheetItemClickListener(this)
        sheetsAdapter!!.setLoans(loansAndClientNames)
        sheetsAdapter!!.setPaymentTypeList(paymentTypeList)
        sheetsAdapter!!.setPaymentTypeOptionsList(sheet.paymentTypeOptions)
        sheetsAdapter!!.notifyDataSetChanged()
    }

    override fun showSuccess() {
        Toaster.show(rootView, getStringMessage(R.string.collectionsheet_submit_success))
    }

    override fun showError(error: String?) {
        Toaster.show(rootView, error)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onShowSheetMandatoryItem(transaction: BulkRepaymentTransactions?, position: Int) {}
    override fun onSaveAdditionalItem(transaction: BulkRepaymentTransactions?, position: Int) {
        payload!!.getBulkRepaymentTransactions()[position] = transaction
    }

    override fun listItemPosition(position: Int) {
        val paymentTypeOptionList = ArrayList(paymentTypeList)
        val paymentTypeOptions = ArrayList(sheet!!.paymentTypeOptions)
        val current = loansAndClientNames!![position]
        val clientId = current!!.id
        val fragment: PaymentDetailsFragment = PaymentDetailsFragment().newInstance(position, payload
                , paymentTypeOptionList, current, paymentTypeOptions, clientId)
        fragment.setTargetFragment(this, requestCode)
        (context as MifosBaseActivity?)!!.replaceFragment(fragment, true, R.id.container)
    }

    private fun submitSheet() {
        if (payload == null) {
            Toaster.show(rootView, getStringMessage(R.string.error_generate_sheet_first))
        } else {
            payload!!.actualDisbursementDate = actualDisbursementDate
            payload!!.transactionDate = transactionDate
            presenter!!.submitIndividualCollectionSheet(payload)
        }
    }

    fun newInstance(sheet: IndividualCollectionSheet?,
                    actualDisbursementDate: String?, transactionDate: String?): IndividualCollectionSheetDetailsFragment {
        val args = Bundle()
        args.putParcelable(Constants.INDIVIDUAL_SHEET, sheet)
        args.putString(Constants.DISBURSEMENT_DATE, actualDisbursementDate)
        args.putString(Constants.TRANSACTION_DATE, transactionDate)
        val fragment = IndividualCollectionSheetDetailsFragment()
        fragment.arguments = args
        return fragment
    }

    companion object {

    }
}