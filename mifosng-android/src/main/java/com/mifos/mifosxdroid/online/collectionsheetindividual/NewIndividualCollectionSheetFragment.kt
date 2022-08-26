package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
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
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject


/**
 * Created by aksh on 18/6/18.
 */
class NewIndividualCollectionSheetFragment : MifosBaseFragment(), IndividualCollectionSheetMvpView, View.OnClickListener {
    @JvmField
    @BindView(R.id.btn_fetch_collection_sheet)
    var btnFetchSheet: Button? = null

    @JvmField
    @BindView(R.id.officeListField)
    var officeListField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.staffSelectionField)
    var staffSelectionField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.repaymentDateFieldContainer)
    var repaymentDateFieldContainer: TextInputLayout? = null

    @JvmField
    @BindView(R.id.btn_clear)
    var btnClear: Button? = null

    @JvmField
    @Inject
    var presenter: NewIndividualCollectionSheetPresenter? = null
    private var sheet: IndividualCollectionSheet? = null
    private var selectedRepaymentDate: Instant = Instant.now()
    private val datePickerDialog by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setSelection(selectedRepaymentDate.toEpochMilli())
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(
                    DateValidatorPointBackward.now()).build())
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    selectedRepaymentDate = Instant.ofEpochMilli(it)
                    val formattedDate = dateFormatter.format(selectedRepaymentDate.atZone(ZoneId.systemDefault()).toLocalDate())
                    repaymentDateFieldContainer?.editText?.setText(formattedDate)
                }
            }
    }
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private var requestPayload: RequestCollectionSheetPayload? = null
    private lateinit var rootView: View
    private var officeNameList: ArrayList<String>? = null
    private var officeList: List<Office>? = null
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
        officeListField!!.setSimpleItems(officeNameList?.toTypedArray() ?: emptyArray())
        officeListField!!.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val i = officeNameList!!.indexOf(adapterView.getItemAtPosition(relativePosition))
            Toaster.show(rootView, officeNameList!![i])
            officeId = officeList!![i].id
            presenter!!.fetchStaff(officeId)
        }
        staffNameList = ArrayList()
        staffSelectionField!!.setSimpleItems(staffNameList?.toTypedArray() ?: emptyArray())
        repaymentDateFieldContainer!!.setEndIconOnClickListener {
            datePickerDialog.show(requireActivity().supportFragmentManager,FragmentConstants.DFRAG_DATE_PICKER)
        }
        btnFetchSheet!!.setOnClickListener(this)
        presenter!!.fetchOffices()

    }

    fun enableOfficeSearch() {
        if (officeSearchDialog == null) {
            val listener = AdapterView.OnItemClickListener { adapterView, view, i, l -> officeListField!!.setSelection(i) }
            officeSearchDialog = SearchDialog(requireContext(), officeNameList, listener)
        }
        officeSearchDialog!!.show()
    }

    fun enableStaffSearch() {
        if (staffSearchDialog == null) {
            val listener = AdapterView.OnItemClickListener { adapterView, view, i, l -> staffSelectionField!!.setSelection(i) }
            staffSearchDialog = SearchDialog(requireContext(), staffNameList, listener)
        }
        staffSearchDialog!!.show()
    }

    fun setRepaymentDate() {
        val formattedDate = dateFormatter.format(selectedRepaymentDate.atZone(ZoneId.systemDefault()).toLocalDate())
        repaymentDateFieldContainer?.editText?.setText(formattedDate)
        transactionDate = formattedDate
        actualDisbursementDate = transactionDate
    }

    private fun prepareRequestPayload() {
        requestPayload = RequestCollectionSheetPayload()
        requestPayload!!.officeId = officeId
        requestPayload!!.staffId = staffId
        requestPayload!!.transactionDate = repaymentDateFieldContainer!!.editText!!.text.toString()
    }

    override fun setOfficeSpinner(offices: List<Office>?) {
        officeList = offices
        officeNameList!!.clear()
        officeNameList!!.addAll(presenter!!.filterOffices(officeList))
        officeListField!!.setSimpleItems(officeNameList?.toTypedArray() ?: emptyArray())
    }

    fun retrieveCollectionSheet() {
        prepareRequestPayload()
        presenter!!.fetchIndividualCollectionSheet(requestPayload)
    }

    override fun setStaffSpinner(staffs: List<Staff>?) {
        staffSelectionField!!.setOnItemClickListener { adapterView, _, relativePosition, _ ->
            val i = staffNameList!!.indexOf(adapterView.getItemAtPosition(relativePosition))
            staffId = staffList!![i].id
        }
        staffList = staffs
        staffNameList!!.clear()
        staffNameList!!.addAll(presenter!!.filterStaff(staffList))
        staffSelectionField?.setSimpleItems(staffNameList?.toTypedArray() ?: emptyArray())
    }


    fun popupDialog() {
        val collectionSheetDialogFragment = CollectionSheetDialogFragment.newInstance(repaymentDateFieldContainer!!.editText!!.text.toString(),
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
            R.id.btn_fetch_collection_sheet -> {
                if(!officeNameList!!.contains(officeListField!!.text.toString()))
                    Toaster.show(rootView, getStringMessage(R.string.error_select_office))
                else if(!staffNameList!!.contains(staffSelectionField!!.text.toString()))
                    Toaster.show(rootView, getStringMessage(R.string.error_select_staff))
                else
                    retrieveCollectionSheet()
            }
        }
    }

    @OnClick(R.id.btn_clear)
    fun clear() {
        officeListField!!.setAdapter(null)
        staffSelectionField!!.setAdapter(null)
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