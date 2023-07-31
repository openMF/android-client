package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mifos.api.model.RequestCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentNewCollectionSheetBinding
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.Constants
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject


/**
 * Created by aksh on 18/6/18.
 */
class NewIndividualCollectionSheetFragment : MifosBaseFragment(), IndividualCollectionSheetMvpView,
    View.OnClickListener {

    private lateinit var binding: FragmentNewCollectionSheetBinding

    @Inject
    lateinit var presenter: NewIndividualCollectionSheetPresenter
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
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            selectedRepaymentDate = Instant.ofEpochMilli(it)
            binding.repaymentDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
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
        presenter.attachView(this)
        setUpUi()
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
            presenter.fetchStaff(officeId!!)
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
        presenter.fetchOffices()

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

    override fun setOfficeSpinner(offices: List<Office>?) {
        if (offices != null) {
            officeList = offices
        }
        officeNameList.clear()
        officeNameList.addAll(presenter.filterOffices(officeList))
        binding.officeListField.setSimpleItems(officeNameList.toTypedArray())
    }

    private fun retrieveCollectionSheet() {
        prepareRequestPayload()
        presenter.fetchIndividualCollectionSheet(requestPayload)
    }

    override fun setStaffSpinner(staffs: List<Staff>?) {
        binding.staffSelectionField.setOnItemClickListener { adapterView, _, relativePosition, _ ->
            val i = staffNameList.indexOf(adapterView.getItemAtPosition(relativePosition))
            staffId = staffList[i].id
        }
        if (staffs != null) {
            staffList = staffs
        }
        staffNameList.clear()
        staffNameList.addAll(presenter.filterStaff(staffList))
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
        Toaster.show(binding.root, message)
    }

    override fun showNoSheetFound() {
        success = false
        Toaster.show(binding.root, getStringMessage(R.string.no_collectionsheet_found))
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