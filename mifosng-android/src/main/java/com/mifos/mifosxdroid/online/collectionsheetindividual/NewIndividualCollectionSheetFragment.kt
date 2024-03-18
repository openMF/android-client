package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentNewCollectionSheetBinding
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale


/**
 * Created by aksh on 18/6/18.
 */
@AndroidEntryPoint
class NewIndividualCollectionSheetFragment : MifosBaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentNewCollectionSheetBinding

    private lateinit var viewModel: NewIndividualCollectionSheetViewModel

    private var sheet: IndividualCollectionSheet? = null
    private var requestPayload: RequestCollectionSheetPayload? = null
    private lateinit var officeNameList: ArrayList<String>
    private var officeList: List<Office> = ArrayList()
    private lateinit var staffNameList: ArrayList<String>
    private var staffList: List<Staff> = ArrayList()
    private var officeId: Int? = 0
    private var staffId: Int? = 0
    private val requestCode = 1
    private var success = true
    private var actualDisbursementDate: String? = null
    private var transactionDate: String? = null

    private var selectedRepaymentDate: Instant = Instant.now()
    private val datePickerDialog by lazy {
        getDatePickerDialog(selectedRepaymentDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(it)
            selectedRepaymentDate = Instant.ofEpochMilli(it)
            binding.repaymentDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            sheet =
                savedInstanceState[Constants.EXTRA_COLLECTION_INDIVIDUAL] as IndividualCollectionSheet
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewCollectionSheetBinding.inflate(inflater, container, false)
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        viewModel = ViewModelProvider(this)[NewIndividualCollectionSheetViewModel::class.java]
        setUpUi()

        viewModel.newIndividualCollectionSheetUiState.observe(viewLifecycleOwner) {
            when (it) {
                is NewIndividualCollectionSheetUiState.SetOfficeSpinner -> {
                    showProgressbar(false)
                    setOfficeSpinner(it.officeList)
                }

                is NewIndividualCollectionSheetUiState.SetStaffSpinner -> {
                    showProgressbar(false)
                    setStaffSpinner(it.staffList)
                }

                is NewIndividualCollectionSheetUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.errorMessage)
                }

                is NewIndividualCollectionSheetUiState.ShowNoSheetFound -> {
                    showProgressbar(false)
                    showNoSheetFound()
                }

                is NewIndividualCollectionSheetUiState.ShowProgressbar -> showProgressbar(true)
                is NewIndividualCollectionSheetUiState.ShowSheet -> {
                    showProgressbar(false)
                    showSheet(it.individualCollectionSheet)
                }

                is NewIndividualCollectionSheetUiState.ShowSuccess -> {
                    showProgressbar(false)
                    showSuccess()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClear.setOnClickListener {
            clear()
        }
    }

    private fun setUpUi() {
        setRepaymentDate()
        officeNameList = ArrayList()
        binding.officeListField.setSimpleItems(officeNameList.toTypedArray())
        binding.officeListField.setOnItemClickListener { adapterView, view, relativePosition, l ->
            val i = officeNameList.indexOf(adapterView.getItemAtPosition(relativePosition))
            Toaster.show(binding.root, officeNameList[i])
            officeId = officeList[i].id
            viewModel.fetchStaff(officeId!!)
        }
        staffNameList = ArrayList()
        binding.staffSelectionField.setSimpleItems(staffNameList.toTypedArray())
        binding.repaymentDateFieldContainer.setEndIconOnClickListener {
            datePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
        binding.btnFetchCollectionSheet.setOnClickListener(this)
        viewModel.fetchOffices()

    }

    private fun setRepaymentDate() {
        binding.repaymentDateFieldContainer.editText?.setText(getTodayFormatted())
        transactionDate = getTodayFormatted()
        actualDisbursementDate = transactionDate
    }

    private fun prepareRequestPayload() {
        requestPayload = RequestCollectionSheetPayload()
        requestPayload?.officeId = officeId
        requestPayload?.staffId = staffId
        requestPayload?.transactionDate =
            binding.repaymentDateFieldContainer.editText?.text.toString()
    }

    private fun setOfficeSpinner(offices: List<Office>?) {
        if (offices != null) {
            officeList = offices
        }
        officeNameList.clear()
        officeNameList.addAll(viewModel.filterOffices(officeList))
        binding.officeListField.setSimpleItems(officeNameList.toTypedArray())
    }

    private fun retrieveCollectionSheet() {
        prepareRequestPayload()
        viewModel.fetchIndividualCollectionSheet(requestPayload)
    }

    private fun setStaffSpinner(staffs: List<Staff>?) {
        binding.staffSelectionField.setOnItemClickListener { adapterView, _, relativePosition, _ ->
            val i = staffNameList.indexOf(adapterView.getItemAtPosition(relativePosition))
            staffId = staffList[i].id
        }
        if (staffs != null) {
            staffList = staffs
        }
        staffNameList.clear()
        staffNameList.addAll(viewModel.filterStaff(staffList))
        binding.staffSelectionField.setSimpleItems(staffNameList.toTypedArray())
    }

    private fun popupDialog() {
        val collectionSheetDialogFragment = CollectionSheetDialogFragment.newInstance(
            binding.repaymentDateFieldContainer.editText?.text.toString(),
            sheet?.clients?.size ?: 0
        )
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
                    ?.supportFragmentManager
                fm?.popBackStack()
                val action = sheet?.let {
                    actualDisbursementDate?.let { it1 ->
                        transactionDate?.let { it2 ->
                            NewIndividualCollectionSheetFragmentDirections.actionNewIndividualCollectionSheetFragmentToIndividualCollectionSheetDetailsFragment(
                                it, it1, it2
                            )
                        }
                    }
                }
                action?.let { findNavController().navigate(it) }
            }
        }
    }

    private fun showSheet(individualCollectionSheet: IndividualCollectionSheet?) {
        sheet = individualCollectionSheet
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            Constants.EXTRA_COLLECTION_INDIVIDUAL,
            sheet
        )
    }

    private fun showSuccess() {
        if (success) {
            popupDialog()
        }
    }

    private fun showError(message: String?) {
        Toaster.show(binding.root, message)
    }

    private fun showNoSheetFound() {
        success = false
        Toaster.show(binding.root, getStringMessage(R.string.no_collectionsheet_found))
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_fetch_collection_sheet -> {
                if (!officeNameList.contains(binding.officeListField.text.toString()))
                    Toaster.show(binding.root, getStringMessage(R.string.error_select_office))
                else if (!staffNameList.contains(binding.staffSelectionField.text.toString()))
                    Toaster.show(binding.root, getStringMessage(R.string.error_select_staff))
                else
                    retrieveCollectionSheet()
            }
        }
    }


    private fun clear() {
        binding.officeListField.setAdapter(null)
        binding.staffSelectionField.setAdapter(null)
        setUpUi()
    }

}