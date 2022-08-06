package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.api.model.RequestCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment
import com.mifos.mifosxdroid.dialogfragments.searchdialog.SearchDialog
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.IndividualCollectionSheetDetailsFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.mifosxdroid.views.CustomSpinner
import com.mifos.mifosxdroid.views.CustomSpinner.OnSpinnerEventsListener
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import java.util.*
import javax.inject.Inject


/**
 * Created by aksh on 18/6/18.
 */
class NewIndividualCollectionSheetFragment : MifosBaseFragment(), IndividualCollectionSheetMvpView, OnDatePickListener, OnItemSelectedListener, View.OnClickListener {
    @JvmField
    @BindView(R.id.btn_fetch_collection_sheet)
    var btnFetchSheet: Button? = null

    @JvmField
    @BindView(R.id.sp_office_list)
    var spOffices: CustomSpinner? = null

    @JvmField
    @BindView(R.id.sp_staff_list)
    var spStaff: CustomSpinner? = null

    @JvmField
    @BindView(R.id.tv_repayment_date)
    var tvRepaymentDate: TextView? = null

    @JvmField
    @BindView(R.id.btn_clear)
    var btnClear: Button? = null

    @JvmField
    @Inject
    var presenter: NewIndividualCollectionSheetPresenter? = null
    private var sheet: IndividualCollectionSheet? = null
    private var datePicker: DialogFragment? = null
    private var requestPayload: RequestCollectionSheetPayload? = null
    private lateinit var rootView: View
    private var officeAdapter: ArrayAdapter<String>? = null
    private var officeNameList: ArrayList<String>? = null
    private var officeList: List<Office>? = null
    private var staffAdapter: ArrayAdapter<String>? = null
    private var staffNameList: ArrayList<String>? = null
    private var staffList: List<Staff>? = null
    private var officeId = 0
    private var staffId = 0
    private val requestCode = 1
    private var success = true
    private var actualDisbursementDate: String? = null
    private var transactionDate: String? = null

    private var officeSearchDialog: SearchDialog? = null
    private var staffSearchDialog: SearchDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (savedInstanceState != null) {
            sheet = savedInstanceState[Constants.EXTRA_COLLECTION_INDIVIDUAL] as IndividualCollectionSheet
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_new_collection_sheet, container, false)
        ButterKnife.bind(this, rootView)
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        presenter!!.attachView(this)
        setUpUi()
        return rootView
    }

    private fun setUpUi() {
        setRepaymentDate()
        officeNameList = ArrayList()
        officeAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, officeNameList ?: emptyList())
        officeAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spOffices!!.adapter = officeAdapter
        spOffices!!.onItemSelectedListener = this
        staffNameList = ArrayList()
        staffAdapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, staffNameList ?: emptyList())
        staffAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStaff!!.adapter = staffAdapter
        tvRepaymentDate!!.setOnClickListener(this)
        btnFetchSheet!!.setOnClickListener(this)
        presenter!!.fetchOffices()

        spOffices!!.setSpinnerEventsListener(object : OnSpinnerEventsListener {
            override fun onSpinnerOpened(spinner: Spinner, isItemListLarge: Boolean) {
                if (isItemListLarge) {
                    enableOfficeSearch()
                }
            }

            override fun onSpinnerClosed(spinner: Spinner) {}
        })

        spStaff!!.setSpinnerEventsListener(object : OnSpinnerEventsListener {
            override fun onSpinnerOpened(spinner: Spinner, isItemListLarge: Boolean) {
                if (isItemListLarge) {
                    enableStaffSearch()
                }
            }

            override fun onSpinnerClosed(spinner: Spinner) {}
        })

    }

    fun enableOfficeSearch() {
        if (officeSearchDialog == null) {
            val listener = AdapterView.OnItemClickListener { adapterView, view, i, l -> spOffices!!.setSelection(i) }
            officeSearchDialog = SearchDialog(requireContext(), officeNameList, listener)
        }
        officeSearchDialog!!.show()
    }

    fun enableStaffSearch() {
        if (staffSearchDialog == null) {
            val listener = AdapterView.OnItemClickListener { adapterView, view, i, l -> spStaff!!.setSelection(i) }
            staffSearchDialog = SearchDialog(requireContext(), staffNameList, listener)
        }
        staffSearchDialog!!.show()
    }

    fun setRepaymentDate() {
        datePicker = MFDatePicker.newInsance(this)
        val date = DateHelper.getDateAsStringUsedForCollectionSheetPayload(MFDatePicker.getDatePickedAsString())
        tvRepaymentDate!!.text = date.replace('-', ' ')
        transactionDate = date.replace('-', ' ')
        actualDisbursementDate = transactionDate
    }

    private fun prepareRequestPayload() {
        requestPayload = RequestCollectionSheetPayload()
        requestPayload!!.officeId = officeId
        requestPayload!!.staffId = staffId
        requestPayload!!.transactionDate = tvRepaymentDate!!.text.toString()
    }

    override fun setOfficeSpinner(offices: List<Office>?) {
        officeList = offices
        officeNameList!!.clear()
        officeNameList!!.add(getString(R.string.spinner_office))
        officeNameList!!.addAll(presenter!!.filterOffices(officeList))
        officeAdapter!!.notifyDataSetChanged()
    }

    override fun onDatePicked(date: String) {
        val d = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
        tvRepaymentDate!!.text = d.replace('-', ' ')
    }

    fun retrieveCollectionSheet() {
        prepareRequestPayload()
        presenter!!.fetchIndividualCollectionSheet(requestPayload)
    }

    fun setTvRepaymentDate() {
        datePicker!!.show(requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun setStaffSpinner(staffs: List<Staff>?) {
        spStaff!!.onItemSelectedListener = this
        staffList = staffs
        staffNameList!!.clear()
        staffNameList!!.add(getString(R.string.spinner_staff))
        staffNameList!!.addAll(presenter!!.filterStaff(staffList))
        staffAdapter!!.notifyDataSetChanged()
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        when (adapterView.id) {
            R.id.sp_office_list -> if (i == officeList!!.size || i == 0) {
                Toaster.show(rootView, getStringMessage(R.string.error_select_office))
            } else {
                Toaster.show(rootView, officeNameList!![i])
                officeId = officeList!![i - 1].id
                presenter!!.fetchStaff(officeId)
            }
            R.id.sp_staff_list -> if (i == staffList!!.size || i == 0) {
                Toaster.show(rootView, getStringMessage(R.string.error_select_staff))
            } else {
                staffId = staffList!![i - 1].id
            }
        }
    }

    fun popupDialog() {
        val collectionSheetDialogFragment = CollectionSheetDialogFragment.newInstance(tvRepaymentDate!!.text.toString(),
                sheet!!.clients.size)
        collectionSheetDialogFragment.setTargetFragment(this, requestCode)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST)
        collectionSheetDialogFragment.show(fragmentTransaction, "Identifier Dialog Fragment")
    }

    fun getResponse(response: String?) {
        when (response) {
            Constants.FILLNOW -> {
                val fm = activity
                        ?.getSupportFragmentManager()
                fm!!.popBackStack()
                val fragment: IndividualCollectionSheetDetailsFragment = IndividualCollectionSheetDetailsFragment().newInstance(sheet,
                        actualDisbursementDate, transactionDate)
                (activity as MifosBaseActivity?)!!.replaceFragment(fragment,
                        true, R.id.container)
            }
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    override fun showSheet(individualCollectionSheet: IndividualCollectionSheet?) {
        sheet = individualCollectionSheet
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Constants.EXTRA_COLLECTION_INDIVIDUAL, sheet)
    }

    override fun showSuccess() {
        if (success) {
            popupDialog()
        }
    }

    override fun showError(message: String?) {
        Toaster.show(rootView, message)
    }

    override fun showNoSheetFound() {
        success = false
        Toaster.show(rootView, getStringMessage(R.string.no_collectionsheet_found))
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_repayment_date -> setTvRepaymentDate()
            R.id.btn_fetch_collection_sheet -> retrieveCollectionSheet()
        }
    }

    @OnClick(R.id.btn_clear)
    fun clear() {
        spOffices!!.adapter = null
        spStaff!!.adapter = null
        setUpUi()
    }

    companion object {
        fun newInstance(): NewIndividualCollectionSheetFragment {
            val args = Bundle()
            val fragment = NewIndividualCollectionSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}