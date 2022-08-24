/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.generatecollectionsheet

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.ClientsAttendance
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.collectionsheet.*
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class GenerateCollectionSheetFragment : MifosBaseFragment(), GenerateCollectionSheetMvpView, View.OnClickListener {
    private val TYPE_LOAN = "1"
    private val TYPE_SAVING = "2"
    private val TAG_TYPE_PRODUCTIVE = 111
    private val TAG_TYPE_COLLECTION = 222

    @JvmField
    @BindView(R.id.officeField)
    var officeField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.staffField)
    var staffField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.centerField)
    var centerField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.groupField)
    var groupField: MaterialAutoCompleteTextView? = null

    @JvmField
    @BindView(R.id.meetingDateFieldContainer)
    var meetingDateFieldContainer: TextInputLayout? = null

    @JvmField
    @BindView(R.id.btn_generate_collection_sheet)
    var btnFetchCollectionSheet: Button? = null

    @JvmField
    @BindView(R.id.btn_generate_productive_collection_sheet)
    var btnFetchProductiveCollectionSheet: Button? = null

    @JvmField
    @BindView(R.id.table_sheet)
    var tableProductive: TableLayout? = null

    @JvmField
    @BindView(R.id.table_additional)
    var tableAdditional: TableLayout? = null

    @JvmField
    @BindView(R.id.btn_submit_productive)
    var btnSubmitProductive: Button? = null

    @JvmField
    @Inject
    var presenter: GenerateCollectionSheetPresenter? = null
    private lateinit var rootView: View
    private var datePickerDialog: MaterialDatePicker<Long>? = null
    private var officeNameIdHashMap = HashMap<String, Int>()
    private var staffNameIdHashMap = HashMap<String, Int>()
    private var centerNameIdHashMap = HashMap<String, Int>()
    private var groupNameIdHashMap = HashMap<String, Int>()
    private var additionalPaymentTypeMap = HashMap<String, Int>()
    private var attendanceTypeOptions = HashMap<String, Int>()
    private val centerNames: List<String?> = ArrayList()
    private val staffNames: List<String?> = ArrayList()
    private val officeNames: List<String?> = ArrayList()
    private val groupNames: List<String?> = ArrayList()
    private val paymentTypes: List<String?> = ArrayList()
    private var officeId = -1
    private var centerId = -1
    private var groupId = -1
    private var staffId = -1

    //id of the center whose Productive CollectionSheet has to be retrieved.
    private var productiveCenterId = -1
    private var calendarId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false)
        ButterKnife.bind(this, rootView)
        presenter!!.attachView(this)
        setUpUi()
        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mItem_search) requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    fun inflateOfficeSpinner() {
        presenter!!.loadOffices()
    }

    fun inflateStaffSpinner(officeId: Int) {
        presenter!!.loadStaffInOffice(officeId)
    }

    fun inflateCenterSpinner(officeId: Int, staffId: Int) {
        val params: MutableMap<String?, Any?> = HashMap()
        params[LIMIT] = -1
        params[ORDER_BY] = ORDER_BY_FIELD_NAME
        params[SORT_ORDER] = ASCENDING
        if (staffId >= 0) {
            params[STAFF_ID] = staffId
        }
        presenter!!.loadCentersInOffice(officeId, params)
    }




    companion object {
        fun newInstance(): GenerateCollectionSheetFragment {
            return GenerateCollectionSheetFragment()
        }
        const val LIMIT = "limit"
        const val ORDER_BY = "orderBy"
        const val SORT_ORDER = "sortOrder"
        const val ASCENDING = "ASC"
        const val ORDER_BY_FIELD_NAME = "name"
        const val STAFF_ID = "staffId"
    }

    fun inflateGroupSpinner(officeId: Int, staffId: Int) {
        val params: MutableMap<String?, Any?> = HashMap()
        params[LIMIT] = -1
        params[ORDER_BY] = ORDER_BY_FIELD_NAME
        params[SORT_ORDER] = ASCENDING
        if (staffId >= 0) params[STAFF_ID] = staffId
        presenter!!.loadGroupsInOffice(officeId, params)
    }

    fun inflateGroupSpinner(centerId: Int) {
        presenter!!.loadGroupByCenter(centerId)
    }

    override fun showOffices(offices: List<Office?>?) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        officeNameIdHashMap = presenter!!.createOfficeNameIdMap(offices as List<Office>?, officeNames as MutableList<String?>)
        officeField!!.setSimpleItems(officeNames.toTypedArray())
        officeField!!.setOnItemClickListener { _, _, i, _ ->
            officeId = officeNameIdHashMap[officeNames[i]]!!
            if (officeId != -1) {
                inflateStaffSpinner(officeId)
                inflateCenterSpinner(officeId, -1)
                inflateGroupSpinner(officeId, -1)
            } else {
                Toaster.show(rootView, getString(R.string.error_select_office))
            }
        }
//        setSpinner(spOffices, officeNames)
//        spOffices!!.onItemSelectedListener = this
    }

    override fun showStaffInOffice(staffs: List<Staff?>?, officeId: Int) {
        this.officeId = officeId
        staffNameIdHashMap = presenter!!.createStaffIdMap(staffs as List<Staff>?, staffNames as MutableList<String?>)
        staffField!!.setSimpleItems(staffNames.toTypedArray())
        staffField!!.setOnItemClickListener { _, _, i, _ ->
            staffId = staffNameIdHashMap[staffNames[i]]!!
            if (staffId != -1) {
                inflateCenterSpinner(officeId, staffId)
                inflateGroupSpinner(officeId, staffId)
            } else {
                Toaster.show(rootView, getString(R.string.error_select_staff))
            }
        }
        staffId = -1 //Reset staff id
    }

    override fun showCentersInOffice(centers: List<Center?>?) {
        centerNameIdHashMap = presenter!!.createCenterIdMap(centers as List<Center>?, centerNames as MutableList<String?>)
        centerField!!.setSimpleItems(centerNames.toTypedArray())
        centerField!!.setOnItemClickListener { _,  _, i, _ ->
            centerId = centerNameIdHashMap.get(centerNames[i])!!
            if (centerId != -1) {
                inflateGroupSpinner(centerId)
            } else {
                Toaster.show(rootView, getString(R.string.error_select_center))
            }
        }
        centerId = -1 //Reset Center id.
    }

    override fun onClick(view: View) {
        when (view.id) {
//            R.id.tv_meeting_date -> setMeetingDate()
            R.id.btn_generate_collection_sheet -> fetchCollectionSheet()
            R.id.btn_generate_productive_collection_sheet -> fetchCenterDetails()
            R.id.btn_submit_productive -> when (view.tag as Int) {
                TAG_TYPE_PRODUCTIVE -> submitProductiveSheet()
                TAG_TYPE_COLLECTION -> submitCollectionSheet()
            }
        }
    }

    private fun setUpUi() {
        inflateOfficeSpinner()
        inflateMeetingDate()
        meetingDateFieldContainer!!.setEndIconOnClickListener {
            datePickerDialog!!.show(requireActivity().supportFragmentManager,FragmentConstants.DFRAG_DATE_PICKER)
        }
        btnFetchProductiveCollectionSheet!!.setOnClickListener(this)
        btnFetchCollectionSheet!!.setOnClickListener(this)
    }

    private fun fetchCollectionSheet() {
        if (groupId == -1) {
            Toaster.show(rootView, getString(R.string.spinner_group))
            return
        }
        val requestPayload = CollectionSheetRequestPayload()
        requestPayload.transactionDate = meetingDateFieldContainer!!.editText!!.text.toString()
        requestPayload.calendarId = calendarId
        presenter!!.loadCollectionSheet(groupId, requestPayload)
    }

    private fun fetchProductiveCollectionSheet() {
        //Make RequestPayload and retrieve Productive CollectionSheet.
        val requestPayload = CollectionSheetRequestPayload()
        requestPayload.transactionDate = meetingDateFieldContainer!!.editText!!.text.toString()
        requestPayload.calendarId = calendarId
        presenter!!.loadProductiveCollectionSheet(productiveCenterId, requestPayload)
    }

    private fun fetchCenterDetails() {
        presenter!!.loadCenterDetails(Constants.DATE_FORMAT_LONG, Constants.LOCALE_EN,
            meetingDateFieldContainer!!.editText!!.text.toString(), officeId, staffId)
    }

    override fun onCenterLoadSuccess(centerDetails: List<CenterDetail?>?) {
        if (centerDetails!!.size == 0) {
            Toaster.show(rootView, getString(R.string.no_collectionsheet_found))
            return
        }

        //Set CalendarId and fetch ProductiveCollectionSheet
        calendarId = centerDetails[0]!!.meetingFallCenters[0].collectionMeetingCalendar.id
        productiveCenterId = centerDetails[0]!!.meetingFallCenters[0].id
        fetchProductiveCollectionSheet()
    }

    override fun showProductive(sheet: CollectionSheetResponse?) {
        inflateProductiveCollectionTable(sheet)
    }

    override fun showCollection(sheet: CollectionSheetResponse?) {
        inflateCollectionTable(sheet)
    }

    private fun inflateCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {
        //Clear old views in case they are present.
        if (tableProductive!!.childCount > 0) {
            tableProductive!!.removeAllViews()
        }

        //A List to be used to inflate Attendance Spinners
        val attendanceTypes = ArrayList<String?>()
        attendanceTypeOptions.clear()
        attendanceTypeOptions = presenter!!.filterAttendanceTypes(collectionSheetResponse
                ?.getAttendanceTypeOptions(), attendanceTypes)
        additionalPaymentTypeMap.clear()
        additionalPaymentTypeMap = presenter!!.filterPaymentTypes(collectionSheetResponse
                ?.getPaymentTypeOptions(), paymentTypes as MutableList<String?>)

        //Add the heading Row
        val headingRow = TableRow(context)
        val headingRowParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        headingRowParams.gravity = Gravity.CENTER
        headingRowParams.setMargins(0, 0, 0, 10)
        headingRow.layoutParams = headingRowParams
        val tvGroupName = TextView(context)
        tvGroupName.text = collectionSheetResponse!!.groups[0].groupName
        tvGroupName.setTypeface(tvGroupName.typeface, Typeface.BOLD)
        tvGroupName.gravity = Gravity.CENTER
        headingRow.addView(tvGroupName)
        for (loanProduct in collectionSheetResponse.loanProducts) {
            val tvProduct = TextView(context)
            tvProduct.text = getString(R.string.collection_loan_product, loanProduct.name)
            tvProduct.setTypeface(tvProduct.typeface, Typeface.BOLD)
            tvProduct.gravity = Gravity.CENTER
            headingRow.addView(tvProduct)
        }
        for (savingsProduct in collectionSheetResponse.savingsProducts) {
            val tvSavingProduct = TextView(context)
            tvSavingProduct.text = getString(R.string.collection_saving_product,
                    savingsProduct.name)
            tvSavingProduct.setTypeface(tvSavingProduct.typeface, Typeface.BOLD)
            tvSavingProduct.gravity = Gravity.CENTER
            headingRow.addView(tvSavingProduct)
        }
        val tvAttendance = TextView(context)
        tvAttendance.text = getString(R.string.attendance)
        tvAttendance.gravity = Gravity.CENTER
        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
        headingRow.addView(tvAttendance)
        tableProductive!!.addView(headingRow)
        for (clientCollectionSheet in collectionSheetResponse
                .groups[0].clients) {
            //Insert rows
            val row = TableRow(context)
            val rowParams = TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            rowParams.gravity = Gravity.CENTER
            rowParams.setMargins(0, 0, 0, 10)
            row.layoutParams = rowParams


            //Column 1: Client Name and Id
            val tvClientName = TextView(context)
            tvClientName.text = concatIdWithName(clientCollectionSheet.clientName,
                    clientCollectionSheet.clientId)
            row.addView(tvClientName)

            //Subsequent columns: The Loan products
            for (loanProduct in collectionSheetResponse.loanProducts) {
                //Since there may be several items in this column, create a container.
                val productContainer = LinearLayout(context)
                productContainer.orientation = LinearLayout.HORIZONTAL

                //Iterate through all the loans in of this type and add in the container
                for (loan in clientCollectionSheet.loans) {
                    if (loanProduct.name == loan.productShortName) {
                        //This loan should be shown in this column. So, add it in the container.
                        val editText = EditText(context)
                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_FLAG_DECIMAL
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
                        //Set the loan id as the Tag of the EditText
                        // in format 'TYPE:ID' which
                        //will later be used as the identifier for this.
                        editText.tag = TYPE_LOAN + ":" + loan.loanId
                        productContainer.addView(editText)
                    }
                }
                row.addView(productContainer)
            }

            //After Loans, show Savings columns
            for (product in collectionSheetResponse.savingsProducts) {
                //Since there may be several Savings items in this column, create a container.
                val productContainer = LinearLayout(context)
                productContainer.orientation = LinearLayout.HORIZONTAL

                //Iterate through all the Savings in of this type and add in the container
                for (saving in clientCollectionSheet.savings) {
                    if (saving.productId == product.id) {
                        //Add the saving in the container
                        val editText = EditText(context)
                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_FLAG_DECIMAL
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
                        //Set the Saving id as the Tag of the EditText
                        // in 'TYPE:ID' format which
                        //will later be used as the identifier for this.
                        editText.tag = TYPE_SAVING + ":" + saving.savingsId
                        productContainer.addView(editText)
                    }
                }
                row.addView(productContainer)
            }
            val spAttendance = Spinner(context)
            //Set the clientId as its tag which will be used as identifier later.
            spAttendance.tag = clientCollectionSheet.clientId
            setSpinner(spAttendance, attendanceTypes)
            row.addView(spAttendance)
            tableProductive!!.addView(row)
        }
        if (btnSubmitProductive!!.visibility != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            btnSubmitProductive!!.visibility = View.VISIBLE
            btnSubmitProductive!!.setOnClickListener(this)
        }

        //If this block has been executed, that means the CollectionSheet
        //which is already shown is for groups.
        btnSubmitProductive!!.tag = TAG_TYPE_COLLECTION
        if (tableAdditional!!.visibility != View.VISIBLE) {
            tableAdditional!!.visibility = View.VISIBLE
        }
        //Show Additional Views
        val rowPayment = TableRow(context)
        val tvLabelPayment = TextView(context)
        tvLabelPayment.text = getString(R.string.payment_type)
        rowPayment.addView(tvLabelPayment)
        val spPayment = Spinner(context)
        setSpinner(spPayment, paymentTypes)
        rowPayment.addView(spPayment)
        tableAdditional!!.addView(rowPayment)
        val rowAccount = TableRow(context)
        val tvLabelAccount = TextView(context)
        tvLabelAccount.text = getString(R.string.account_number)
        rowAccount.addView(tvLabelAccount)
        val etPayment = EditText(context)
        rowAccount.addView(etPayment)
        tableAdditional!!.addView(rowAccount)
        val rowCheck = TableRow(context)
        val tvLabelCheck = TextView(context)
        tvLabelCheck.text = getString(R.string.cheque_number)
        rowCheck.addView(tvLabelCheck)
        val etCheck = EditText(context)
        rowCheck.addView(etCheck)
        tableAdditional!!.addView(rowCheck)
        val rowRouting = TableRow(context)
        val tvLabelRouting = TextView(context)
        tvLabelRouting.text = getString(R.string.routing_code)
        rowRouting.addView(tvLabelRouting)
        val etRouting = EditText(context)
        rowRouting.addView(etRouting)
        tableAdditional!!.addView(rowRouting)
        val rowReceipt = TableRow(context)
        val tvLabelReceipt = TextView(context)
        tvLabelReceipt.text = getString(R.string.receipt_number)
        rowReceipt.addView(tvLabelReceipt)
        val etReceipt = EditText(context)
        rowReceipt.addView(etReceipt)
        tableAdditional!!.addView(rowReceipt)
        val rowBank = TableRow(context)
        val tvLabelBank = TextView(context)
        tvLabelBank.text = getString(R.string.bank_number)
        rowBank.addView(tvLabelBank)
        val etBank = EditText(context)
        rowBank.addView(etBank)
        tableAdditional!!.addView(rowBank)
    }


    private fun inflateProductiveCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {

        //Clear old views in case they are present.
        if (tableProductive!!.childCount > 0) {
            tableProductive!!.removeAllViews()
        }
        if (tableAdditional!!.visibility == View.VISIBLE) {
            tableAdditional!!.removeAllViews()
            tableAdditional!!.visibility = View.GONE
        }

        //A List to be used to inflate Attendance Spinners
        val attendanceTypes = ArrayList<String?>()
        attendanceTypeOptions.clear()
        attendanceTypeOptions = presenter!!.filterAttendanceTypes(collectionSheetResponse
                ?.getAttendanceTypeOptions(), attendanceTypes)

        //Add the heading Row
        val headingRow = TableRow(context)
        val headingRowParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        headingRowParams.gravity = Gravity.CENTER
        headingRowParams.setMargins(0, 0, 0, 10)
        headingRow.layoutParams = headingRowParams
        val tvGroupName = TextView(context)
        tvGroupName.text = collectionSheetResponse!!.groups[0].groupName
        tvGroupName.setTypeface(tvGroupName.typeface, Typeface.BOLD)
        tvGroupName.gravity = Gravity.CENTER
        headingRow.addView(tvGroupName)
        for (loanProduct in collectionSheetResponse.loanProducts) {
            val tvProduct = TextView(context)
            tvProduct.text = getString(R.string.collection_heading_charges,
                    loanProduct.name)
            tvProduct.setTypeface(tvProduct.typeface, Typeface.BOLD)
            tvProduct.gravity = Gravity.CENTER
            headingRow.addView(tvProduct)
        }
        val tvAttendance = TextView(context)
        tvAttendance.text = getString(R.string.attendance)
        tvAttendance.gravity = Gravity.CENTER
        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
        headingRow.addView(tvAttendance)
        tableProductive!!.addView(headingRow)
        for (clientCollectionSheet in collectionSheetResponse.groups[0].clients) {
            //Insert rows
            val row = TableRow(context)
            val rowParams = TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            rowParams.gravity = Gravity.CENTER
            rowParams.setMargins(0, 0, 0, 10)
            row.layoutParams = rowParams


            //Column 1: Client Name and Id
            val tvClientName = TextView(context)
            tvClientName.text = concatIdWithName(clientCollectionSheet.clientName,
                    clientCollectionSheet.clientId)
            row.addView(tvClientName)

            //Subsequent columns: The Loan products
            for (loanProduct in collectionSheetResponse.loanProducts) {
                //Since there may be several items in this column, create a container.
                val productContainer = LinearLayout(context)
                productContainer.orientation = LinearLayout.HORIZONTAL

                //Iterate through all the loans in of this type and add in the container
                for (loanCollectionSheet in clientCollectionSheet.loans) {
                    if (loanProduct.name == loanCollectionSheet.productShortName) {
                        //This loan should be shown in this column. So, add it in the container.
                        val editText = EditText(context)
                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
                                InputType.TYPE_NUMBER_FLAG_DECIMAL
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
                        //Set the loan id as the Tag of the EditText which
                        //will later be used as the identifier for this.
                        editText.tag = TYPE_LOAN + ":" + loanCollectionSheet.loanId
                        productContainer.addView(editText)
                    }
                }
                row.addView(productContainer)
            }
            val spAttendance = Spinner(context)
            setSpinner(spAttendance, attendanceTypes)
            row.addView(spAttendance)
            tableProductive!!.addView(row)
        }
        if (btnSubmitProductive!!.visibility != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            btnSubmitProductive!!.visibility = View.VISIBLE
            btnSubmitProductive!!.setOnClickListener(this)
        }

        //If this block has been executed, that the CollectionSheet
        //which is already shown on screen is for center - Productive.
        btnSubmitProductive!!.tag = TAG_TYPE_PRODUCTIVE
    }

    private fun submitProductiveSheet() {
        val payload = ProductiveCollectionSheetPayload()
        payload.calendarId = calendarId
        payload.transactionDate = meetingDateFieldContainer!!.editText!!.text.toString()
        for (i in 0 until tableProductive!!.childCount) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.
            val row = tableProductive!!.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                //In a particular TableRow
                //Loop through the views and check if it's LinearLayout
                //because the required views - EditTexts are there only.
                val v = row.getChildAt(j)
                if (v is LinearLayout) {
                    //So, we got into the container containing the EditTexts
                    //Now, extract the values and the loanId associated to this
                    //particular TextView which was set as its Tag.
                    for (k in 0 until v.childCount) {
                        val et = v.getChildAt(k) as EditText
                        val typeId = et.tag.toString().split(":").toTypedArray()
                        when (typeId[0]) {
                            TYPE_LOAN -> {
                                val loanId = typeId[1].toInt()
                                val amount = et.text.toString().toDouble()
                                payload.bulkRepaymentTransactions.add(
                                        BulkRepaymentTransactions(loanId, amount))
                            }
                        }
                    }
                }
            }
        }

        //Payload with all the items is ready. Now, hit the endpoint and submit it.
        presenter!!.submitProductiveSheet(productiveCenterId, payload)
    }

    private fun submitCollectionSheet() {
        val payload = CollectionSheetPayload()
        payload.calendarId = calendarId
        payload.transactionDate = meetingDateFieldContainer!!.editText!!.text.toString()
        payload.actualDisbursementDate = meetingDateFieldContainer!!.editText!!.text.toString()
        for (i in 0 until tableProductive!!.childCount) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.
            val row = tableProductive!!.getChildAt(i) as TableRow
            for (j in 0 until row.childCount) {
                //In a particular TableRow
                //Loop through the views and check if it's LinearLayout
                //because the required views - EditTexts are there only.
                val v = row.getChildAt(j)
                if (v is LinearLayout) {
                    //So, we got into the container containing the EditTexts
                    //Now, extract the values and the loanId associated to this
                    //particular TextView which was set as its Tag.
                    for (k in 0 until v.childCount) {
                        val et = v.getChildAt(k) as EditText
                        val typeId = et.tag.toString().split(":").toTypedArray()
                        when (typeId[0]) {
                            TYPE_LOAN -> {
                                val loanId = typeId[1].toInt()
                                val amount = et.text.toString().toDouble()
                                payload.bulkRepaymentTransactions.add(
                                        BulkRepaymentTransactions(loanId, amount))
                            }
                            TYPE_SAVING -> {
                                //Add to Savings
                                val savingsId = typeId[1].toInt()
                                val amountSaving = et.text.toString()
                                payload.bulkSavingsDueTransactions.add(
                                        BulkSavingsDueTransaction(savingsId, amountSaving))
                            }
                        }
                    }
                } else if (v is Spinner) {
                    //Attendance
                    val spinner = v
                    val clientId = spinner.tag as Int
                    val attendanceTypeId = attendanceTypeOptions[spinner.selectedItem.toString()]!!
                    payload.clientsAttendance
                            .add(ClientsAttendance(clientId, attendanceTypeId))
                }
            }
        }

        //Check if Additional details are there
        if (tableAdditional != null && tableAdditional!!.childCount > 0) {
            for (i in 0..5) {
                val row = tableAdditional!!.getChildAt(i) as TableRow
                val v = row.getChildAt(1)
                if (v is Spinner) {
                    val paymentId = additionalPaymentTypeMap[v.selectedItem.toString()]!!
                    if (paymentId != -1) {
                        payload.paymentTypeId = paymentId
                    }
                } else if (v is EditText) {
                    val value = v.text.toString()
                    if (value != "") {
                        when (i) {
                            1 -> payload.accountNumber = value
                            2 -> payload.checkNumber = value
                            3 -> payload.routingCode = value
                            4 -> payload.receiptNumber = value
                            5 -> payload.bankNumber = value
                        }
                    }
                }
            }
        }

        //Payload with all the items is ready. Now, hit the endpoint and submit it.
        presenter!!.submitCollectionSheet(groupId, payload)
    }

    private fun concatIdWithName(name: String, id: Int): String {
        return "($id)$name"
    }

    private var selectedMeetingDate: Instant = Instant.now();

    private fun inflateMeetingDate() {
        datePickerDialog = MaterialDatePicker.Builder.datePicker()
            .setSelection(selectedMeetingDate.toEpochMilli())
            .setCalendarConstraints(CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.now()).build())
            .build()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        datePickerDialog!!.addOnPositiveButtonClickListener {
            selectedMeetingDate = Instant.ofEpochMilli(it)
            val formattedDate = formatter.format(selectedMeetingDate.atZone(ZoneId.systemDefault()).toLocalDate())
            meetingDateFieldContainer?.editText?.setText(formattedDate)
        }

        val formattedDate = formatter.format(selectedMeetingDate.atZone(ZoneId.systemDefault()).toLocalDate())
        meetingDateFieldContainer?.editText?.setText(formattedDate)
    }

    override fun showGroupsInOffice(groups: List<Group?>?) {
        groupNameIdHashMap = presenter!!.createGroupIdMap(groups as List<Group>?, groupNames as MutableList<String?>)
        groupField!!.setSimpleItems(groupNames.toTypedArray())
    }

    override fun showGroupByCenter(centerWithAssociations: CenterWithAssociations?) {
        groupNameIdHashMap = presenter!!.createGroupIdMap(
                centerWithAssociations!!.groupMembers, groupNames as MutableList<String?>)
        groupField!!.setSimpleItems(groupNames.toTypedArray())
        groupField!!.setOnItemClickListener { _, _, i, _ ->
            groupId = groupNameIdHashMap[groupNames[i]]!!
            if (groupId == -1) {
                Toaster.show(rootView, getString(R.string.error_select_group))
            }
        }
        calendarId = centerWithAssociations.collectionMeetingCalendar.id
        groupId = -1 //Reset group Id
    }

    private fun setSpinner(spinner: Spinner?, values: List<String?>) {
        val adapter = ArrayAdapter(requireActivity(),
                android.R.layout.simple_spinner_item, values)
        adapter.notifyDataSetChanged()
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
    }

    override fun showError(s: String?) {
        Toaster.show(rootView, s)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter!!.detachView()
    }
}