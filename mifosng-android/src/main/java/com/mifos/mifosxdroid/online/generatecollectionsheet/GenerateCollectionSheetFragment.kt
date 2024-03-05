/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.generatecollectionsheet

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.model.ClientsAttendance
import com.mifos.core.objects.collectionsheet.BulkSavingsDueTransaction
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentGenerateCollectionSheetBinding
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

@AndroidEntryPoint
class GenerateCollectionSheetFragment : MifosBaseFragment(),
    View.OnClickListener {

    private lateinit var binding: FragmentGenerateCollectionSheetBinding

    private val TYPE_LOAN = "1"
    private val TYPE_SAVING = "2"
    private val TAG_TYPE_PRODUCTIVE = 111
    private val TAG_TYPE_COLLECTION = 222

    private lateinit var viewModel: GenerateCollectionSheetViewModel

    private var officeNameIdHashMap = HashMap<String?, Int?>()
    private var staffNameIdHashMap = HashMap<String?, Int?>()
    private var centerNameIdHashMap = HashMap<String?, Int?>()
    private var groupNameIdHashMap = HashMap<String?, Int?>()
    private var additionalPaymentTypeMap = HashMap<String, Int>()
    private var attendanceTypeOptions = HashMap<String?, Int>()
    private val centerNames: List<String?> = ArrayList()
    private val staffNames: List<String?> = ArrayList()
    private val officeNames: List<String?> = ArrayList()
    private val groupNames: List<String?> = ArrayList()
    private val paymentTypes: List<String?> = ArrayList()
    private var officeId = -1
    private var centerId = -1
    private var groupId = -1
    private var staffId = -1

    private var selectedRepaymentDate: Instant = Instant.now()
    private val datePickerDialog by lazy {
        getDatePickerDialog(selectedRepaymentDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            selectedRepaymentDate = Instant.ofEpochMilli(it)
            binding.meetingDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    //id of the center whose Productive CollectionSheet has to be retrieved.
    private var productiveCenterId: Int? = -1
    private var calendarId: Int? = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenerateCollectionSheetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GenerateCollectionSheetViewModel::class.java]
        setUpUi()

        viewModel.generateCollectionSheetUiState.observe(viewLifecycleOwner) {
            when (it) {
                is GenerateCollectionSheetUiState.OnCenterLoadSuccess -> {
                    showProgressbar(false)
                    onCenterLoadSuccess(it.centerDetails)
                }

                is GenerateCollectionSheetUiState.ShowCentersInOffice -> {
                    showProgressbar(false)
                    showCentersInOffice(it.centers)
                }

                is GenerateCollectionSheetUiState.ShowCollection -> {
                    showProgressbar(false)
                    showCollection(it.collectionSheetResponse)
                }

                is GenerateCollectionSheetUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is GenerateCollectionSheetUiState.ShowGroupByCenter -> {
                    showProgressbar(false)
                    showGroupByCenter(it.centerWithAssociations)
                }

                is GenerateCollectionSheetUiState.ShowGroupsInOffice -> {
                    showProgressbar(false)
                    showGroupsInOffice(it.groups)
                }

                is GenerateCollectionSheetUiState.ShowOffices -> {
                    showProgressbar(false)
                    showOffices(it.offices)
                }

                is GenerateCollectionSheetUiState.ShowProductive -> {
                    showProgressbar(false)
                    showProductive(it.collectionSheetResponse)
                }

                is GenerateCollectionSheetUiState.ShowProgressbar -> showProgressbar(true)
                is GenerateCollectionSheetUiState.ShowStaffInOffice -> {
                    showProgressbar(false)
                    showStaffInOffice(it.staffs, it.officeId)
                }
            }
        }

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mItem_search) requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    private fun inflateOfficeSpinner() {
        viewModel.loadOffices()
    }

    private fun inflateStaffSpinner(officeId: Int) {
        viewModel.loadStaffInOffice(officeId)
    }

    private fun inflateCenterSpinner(officeId: Int, staffId: Int) {
        val params: MutableMap<String, String> = HashMap()
        params[LIMIT] = "-1"
        params[ORDER_BY] = ORDER_BY_FIELD_NAME
        params[SORT_ORDER] = ASCENDING
        if (staffId >= 0) {
            params[STAFF_ID] = staffId.toString()
        }
        viewModel.loadCentersInOffice(officeId, params)
    }


    companion object {
        const val LIMIT = "limit"
        const val ORDER_BY = "orderBy"
        const val SORT_ORDER = "sortOrder"
        const val ASCENDING = "ASC"
        const val ORDER_BY_FIELD_NAME = "name"
        const val STAFF_ID = "staffId"
    }

    private fun inflateGroupSpinner(officeId: Int, staffId: Int) {
        val params: MutableMap<String, String> = HashMap()
        params[LIMIT] = "-1"
        params[ORDER_BY] = ORDER_BY_FIELD_NAME
        params[SORT_ORDER] = ASCENDING
        if (staffId >= 0) params[STAFF_ID] = staffId.toString()
        viewModel.loadGroupsInOffice(officeId, params)
    }

    private fun inflateGroupSpinner(centerId: Int) {
        viewModel.loadGroupByCenter(centerId)
    }

    private fun showOffices(offices: List<Office>) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        officeNameIdHashMap =
            viewModel.createOfficeNameIdMap(offices, officeNames as MutableList<String?>)
        binding.officeField.setSimpleItems(officeNames.toTypedArray())
        binding.officeField.setOnItemClickListener { _, _, i, _ ->
            officeId = officeNameIdHashMap[officeNames[i]]!!
            if (officeId != -1) {
                inflateStaffSpinner(officeId)
                inflateCenterSpinner(officeId, -1)
                inflateGroupSpinner(officeId, -1)
            } else {
                Toaster.show(binding.root, getString(R.string.error_select_office))
            }
        }
    }

    private fun showStaffInOffice(staffs: List<Staff>, officeId: Int) {
        this.officeId = officeId
        staffNameIdHashMap = viewModel.createStaffIdMap(staffs, staffNames as MutableList<String?>)
        binding.staffField.setSimpleItems(staffNames.toTypedArray())
        binding.staffField.setOnItemClickListener { _, _, i, _ ->
            staffId = staffNameIdHashMap[staffNames[i]]!!
            if (staffId != -1) {
                inflateCenterSpinner(officeId, staffId)
                inflateGroupSpinner(officeId, staffId)
            } else {
                Toaster.show(binding.root, getString(R.string.error_select_staff))
            }
        }
        staffId = -1 //Reset staff id
    }

    private fun showCentersInOffice(centers: List<Center>) {
        centerNameIdHashMap =
            viewModel.createCenterIdMap(centers, centerNames as MutableList<String?>)
        binding.centerField.setSimpleItems(centerNames.toTypedArray())
        binding.centerField.setOnItemClickListener { _, _, i, _ ->
            centerId = centerNameIdHashMap[centerNames[i]]!!
            if (centerId != -1) {
                inflateGroupSpinner(centerId)
            } else {
                Toaster.show(binding.root, getString(R.string.error_select_center))
            }
        }
        centerId = -1 //Reset Center id.
    }

    override fun onClick(view: View) {
        when (view.id) {
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
        binding.meetingDateFieldContainer.setOnClickListener(this)
        binding.btnGenerateProductiveCollectionSheet.setOnClickListener(this)
        binding.btnGenerateCollectionSheet.setOnClickListener(this)
    }

    private fun fetchCollectionSheet() {
        if (groupId == -1) {
            Toaster.show(binding.root, getString(R.string.spinner_group))
            return
        }
        val requestPayload = CollectionSheetRequestPayload()
        requestPayload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
        requestPayload.calendarId = calendarId
        viewModel.loadCollectionSheet(groupId, requestPayload)
    }

    private fun fetchProductiveCollectionSheet() {
        //Make RequestPayload and retrieve Productive CollectionSheet.
        val requestPayload = CollectionSheetRequestPayload()
        requestPayload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
        requestPayload.calendarId = calendarId
        productiveCenterId?.let { viewModel.loadProductiveCollectionSheet(it, requestPayload) }
    }

    private fun fetchCenterDetails() {
        viewModel.loadCenterDetails(
            Constants.DATE_FORMAT_LONG, Constants.LOCALE_EN,
            binding.meetingDateFieldContainer.editText?.text.toString(), officeId, staffId
        )
    }

    private fun onCenterLoadSuccess(centerDetails: List<CenterDetail>) {
        if (centerDetails.isEmpty()) {
            Toaster.show(binding.root, getString(R.string.no_collectionsheet_found))
            return
        }

        //Set CalendarId and fetch ProductiveCollectionSheet
        calendarId = centerDetails[0].meetingFallCenters?.get(0)?.collectionMeetingCalendar?.id
        productiveCenterId = centerDetails[0].meetingFallCenters?.get(0)?.id
        fetchProductiveCollectionSheet()
    }

    private fun showProductive(sheet: CollectionSheetResponse) {
        inflateProductiveCollectionTable(sheet)
    }

    private fun showCollection(sheet: CollectionSheetResponse) {
        inflateCollectionTable(sheet)
    }

    private fun inflateCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {
        //Clear old views in case they are present.
        if (binding.tableSheet.childCount > 0) {
            binding.tableSheet.removeAllViews()
        }

        //A List to be used to inflate Attendance Spinners
        val attendanceTypes = ArrayList<String?>()
        attendanceTypeOptions.clear()
        attendanceTypeOptions = viewModel.filterAttendanceTypes(
            collectionSheetResponse
                ?.attendanceTypeOptions, attendanceTypes
        )
        additionalPaymentTypeMap.clear()
        additionalPaymentTypeMap = viewModel.filterPaymentTypes(
            collectionSheetResponse
                ?.paymentTypeOptions, paymentTypes as MutableList<String?>
        )

        //Add the heading Row
        val headingRow = TableRow(context)
        val headingRowParams = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
            tvSavingProduct.text = getString(
                R.string.collection_saving_product,
                savingsProduct.name
            )
            tvSavingProduct.setTypeface(tvSavingProduct.typeface, Typeface.BOLD)
            tvSavingProduct.gravity = Gravity.CENTER
            headingRow.addView(tvSavingProduct)
        }
        val tvAttendance = TextView(context)
        tvAttendance.text = getString(R.string.attendance)
        tvAttendance.gravity = Gravity.CENTER
        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
        headingRow.addView(tvAttendance)
        binding.tableSheet.addView(headingRow)
        for (clientCollectionSheet in collectionSheetResponse
            .groups[0].clients) {
            //Insert rows
            val row = TableRow(context)
            val rowParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rowParams.gravity = Gravity.CENTER
            rowParams.setMargins(0, 0, 0, 10)
            row.layoutParams = rowParams


            //Column 1: Client Name and Id
            val tvClientName = TextView(context)
            tvClientName.text = clientCollectionSheet.clientName?.let {
                concatIdWithName(
                    it,
                    clientCollectionSheet.clientId
                )
            }
            row.addView(tvClientName)

            //Subsequent columns: The Loan products
            for (loanProduct in collectionSheetResponse.loanProducts) {
                //Since there may be several items in this column, create a container.
                val productContainer = LinearLayout(context)
                productContainer.orientation = LinearLayout.HORIZONTAL

                //Iterate through all the loans in of this type and add in the container
                for (loan in clientCollectionSheet.loans!!) {
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
            binding.tableSheet.addView(row)
        }
        if (binding.btnSubmitProductive.visibility != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            binding.btnSubmitProductive.visibility = View.VISIBLE
            binding.btnSubmitProductive.setOnClickListener(this)
        }

        //If this block has been executed, that means the CollectionSheet
        //which is already shown is for groups.
        binding.btnSubmitProductive.tag = TAG_TYPE_COLLECTION
        if (binding.tableAdditional.visibility != View.VISIBLE) {
            binding.tableAdditional.visibility = View.VISIBLE
        }
        //Show Additional Views
        val rowPayment = TableRow(context)
        val tvLabelPayment = TextView(context)
        tvLabelPayment.text = getString(R.string.payment_type)
        rowPayment.addView(tvLabelPayment)
        binding.tableAdditional.addView(rowPayment)
        val rowAccount = TableRow(context)
        val tvLabelAccount = TextView(context)
        tvLabelAccount.text = getString(R.string.account_number)
        rowAccount.addView(tvLabelAccount)
        val etPayment = EditText(context)
        rowAccount.addView(etPayment)
        binding.tableAdditional.addView(rowAccount)
        val rowCheck = TableRow(context)
        val tvLabelCheck = TextView(context)
        tvLabelCheck.text = getString(R.string.cheque_number)
        rowCheck.addView(tvLabelCheck)
        val etCheck = EditText(context)
        rowCheck.addView(etCheck)
        binding.tableAdditional.addView(rowCheck)
        val rowRouting = TableRow(context)
        val tvLabelRouting = TextView(context)
        tvLabelRouting.text = getString(R.string.routing_code)
        rowRouting.addView(tvLabelRouting)
        val etRouting = EditText(context)
        rowRouting.addView(etRouting)
        binding.tableAdditional.addView(rowRouting)
        val rowReceipt = TableRow(context)
        val tvLabelReceipt = TextView(context)
        tvLabelReceipt.text = getString(R.string.receipt_number)
        rowReceipt.addView(tvLabelReceipt)
        val etReceipt = EditText(context)
        rowReceipt.addView(etReceipt)
        binding.tableAdditional.addView(rowReceipt)
        val rowBank = TableRow(context)
        val tvLabelBank = TextView(context)
        tvLabelBank.text = getString(R.string.bank_number)
        rowBank.addView(tvLabelBank)
        val etBank = EditText(context)
        rowBank.addView(etBank)
        binding.tableAdditional.addView(rowBank)
    }


    private fun inflateProductiveCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {

        //Clear old views in case they are present.
        if (binding.tableSheet.childCount > 0) {
            binding.tableSheet.removeAllViews()
        }
        if (binding.tableAdditional.visibility == View.VISIBLE) {
            binding.tableAdditional.removeAllViews()
            binding.tableAdditional.visibility = View.GONE
        }

        //A List to be used to inflate Attendance Spinners
        val attendanceTypes = ArrayList<String?>()
        attendanceTypeOptions.clear()
        attendanceTypeOptions = viewModel.filterAttendanceTypes(
            collectionSheetResponse
                ?.attendanceTypeOptions, attendanceTypes
        )

        //Add the heading Row
        val headingRow = TableRow(context)
        val headingRowParams = TableRow.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
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
            tvProduct.text = getString(
                R.string.collection_heading_charges,
                loanProduct.name
            )
            tvProduct.setTypeface(tvProduct.typeface, Typeface.BOLD)
            tvProduct.gravity = Gravity.CENTER
            headingRow.addView(tvProduct)
        }
        val tvAttendance = TextView(context)
        tvAttendance.text = getString(R.string.attendance)
        tvAttendance.gravity = Gravity.CENTER
        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
        headingRow.addView(tvAttendance)
        binding.tableSheet.addView(headingRow)
        for (clientCollectionSheet in collectionSheetResponse.groups[0].clients) {
            //Insert rows
            val row = TableRow(context)
            val rowParams = TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            rowParams.gravity = Gravity.CENTER
            rowParams.setMargins(0, 0, 0, 10)
            row.layoutParams = rowParams


            //Column 1: Client Name and Id
            val tvClientName = TextView(context)
            tvClientName.text = clientCollectionSheet.clientName?.let {
                concatIdWithName(
                    it,
                    clientCollectionSheet.clientId
                )
            }
            row.addView(tvClientName)

            //Subsequent columns: The Loan products
            for (loanProduct in collectionSheetResponse.loanProducts) {
                //Since there may be several items in this column, create a container.
                val productContainer = LinearLayout(context)
                productContainer.orientation = LinearLayout.HORIZONTAL

                //Iterate through all the loans in of this type and add in the container
                for (loanCollectionSheet in clientCollectionSheet.loans!!) {
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
            binding.tableSheet.addView(row)
        }
        if (binding.btnSubmitProductive.visibility != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            binding.btnSubmitProductive.visibility = View.VISIBLE
            binding.btnSubmitProductive.setOnClickListener(this)
        }

        //If this block has been executed, that the CollectionSheet
        //which is already shown on screen is for center - Productive.
        binding.btnSubmitProductive.tag = TAG_TYPE_PRODUCTIVE
    }

    private fun submitProductiveSheet() {
        val payload = ProductiveCollectionSheetPayload()
        payload.calendarId = calendarId
        payload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
        for (i in 0 until binding.tableSheet.childCount) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.
            val row = binding.tableSheet.getChildAt(i) as TableRow
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
                                    BulkRepaymentTransactions(loanId, amount)
                                )
                            }
                        }
                    }
                }
            }
        }

        //Payload with all the items is ready. Now, hit the endpoint and submit it.
        productiveCenterId?.let { viewModel.submitProductiveSheet(it, payload) }
    }

    private fun submitCollectionSheet() {
        val payload = CollectionSheetPayload()
        payload.calendarId = calendarId
        payload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
        payload.actualDisbursementDate = binding.meetingDateFieldContainer.editText?.text.toString()
        for (i in 0 until binding.tableSheet.childCount) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.
            val row = binding.tableSheet.getChildAt(i) as TableRow
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
                                    BulkRepaymentTransactions(loanId, amount)
                                )
                            }

                            TYPE_SAVING -> {
                                //Add to Savings
                                val savingsId = typeId[1].toInt()
                                val amountSaving = et.text.toString()
                                payload.bulkSavingsDueTransactions.add(
                                    BulkSavingsDueTransaction(savingsId, amountSaving)
                                )
                            }
                        }
                    }
                } else if (v is Spinner) {
                    //Attendance
                    val clientId = v.tag as Int
                    val attendanceTypeId = attendanceTypeOptions[v.selectedItem.toString()]!!
                    payload.clientsAttendance
                        .add(ClientsAttendance(clientId, attendanceTypeId))
                }
            }
        }

        //Check if Additional details are there
        if (binding.tableAdditional.childCount > 0) {
            for (i in 0..5) {
                val row = binding.tableAdditional.getChildAt(i) as TableRow
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
        viewModel.submitCollectionSheet(groupId, payload)
    }

    private fun concatIdWithName(name: String, id: Int): String {
        return "($id)$name"
    }

    private fun inflateMeetingDate() {
        binding.meetingDateFieldContainer.editText?.setText(getTodayFormatted())
        binding.meetingDateFieldContainer.setEndIconOnClickListener {
            datePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    private fun showGroupsInOffice(groups: List<Group>) {
        groupNameIdHashMap = viewModel.createGroupIdMap(groups, groupNames as MutableList<String?>)
        binding.groupField.setSimpleItems(groupNames.toTypedArray())
    }

    private fun showGroupByCenter(centerWithAssociations: CenterWithAssociations) {
        groupNameIdHashMap = viewModel.createGroupIdMap(
            centerWithAssociations.groupMembers, groupNames as MutableList<String?>
        )
        binding.groupField.setSimpleItems(groupNames.toTypedArray())
        binding.groupField.setOnItemClickListener { _, _, i, _ ->
            groupId = groupNameIdHashMap[groupNames[i]]!!
            if (groupId == -1) {
                Toaster.show(binding.root, getString(R.string.error_select_group))
            }
        }
    }

    private fun showError(s: String?) {
        Toaster.show(binding.root, s)
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }
}