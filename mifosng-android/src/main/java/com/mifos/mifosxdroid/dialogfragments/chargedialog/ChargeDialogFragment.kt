/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.dialogfragments.chargedialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.templates.clients.ChargeTemplate
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.DialogFragmentChargeBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.Companion.newInsance
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
@AndroidEntryPoint
class ChargeDialogFragment : ProgressableDialogFragment(), OnDatePickListener,
    OnItemSelectedListener {
    val LOG_TAG = javaClass.simpleName

    private lateinit var binding: DialogFragmentChargeBinding

    private lateinit var viewModel: ChargeDialogViewModel

    private var chargeCreateListener: OnChargeCreateListener? = null
    private val chargeNameList: MutableList<String> = ArrayList()
    private lateinit var chargeNameAdapter: ArrayAdapter<String>
    private var mChargeTemplate: ChargeTemplate? = null
    private lateinit var dueDateString: String
    private var dueDateAsIntegerList: List<Int>? = null
    private var mfDatePicker: DialogFragment? = null
    private var chargeId = 0
    private var chargeName: String? = null
    private var clientId = 0
    private var createdCharge: Charges? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) clientId = requireArguments().getInt(Constants.CLIENT_ID)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) clientId = requireArguments().getInt(Constants.CLIENT_ID)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        if (requireActivity().actionBar != null) requireActivity().actionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        binding = DialogFragmentChargeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ChargeDialogViewModel::class.java]
        inflateDueDate()
        inflateChargesSpinner()
        inflateChargeNameSpinner()

        viewModel.chargeDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is ChargeDialogUiState.ShowAllChargesV2 -> {
                    showProgressbar(false)
                    showAllChargesV2(it.chargeTemplate)
                }

                is ChargeDialogUiState.ShowChargesCreatedSuccessfully -> {
                    showProgressbar(false)
                    showChargesCreatedSuccessfully(it.chargeCreationResponse)
                }

                is ChargeDialogUiState.ShowFetchingError -> {
                    showProgressbar(false)
                    showFetchingError(it.message)
                }

                is ChargeDialogUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btSaveCharge.setOnClickListener {
            saveNewCharge()
        }

        binding.etDate.setOnClickListener {
            inflateDatePicker()
        }
    }

    fun saveNewCharge() {
        //Insert values for the new Charge.
        if (binding.amountDueCharge.text.toString().isEmpty()) {
            show(
                binding.root, getString(R.string.amount)
                        + " " + getString(R.string.error_cannot_be_empty)
            )
            return
        }
        createdCharge = Charges()
        createdCharge?.chargeId = chargeId
        createdCharge?.amount = binding.amountDueCharge.editableText.toString().toDouble()
        dueDateAsIntegerList = DateHelper.convertDateAsReverseInteger(dueDateString)
        createdCharge?.dueDate = dueDateAsIntegerList as List<Int>
        createdCharge?.name = chargeName
        val chargesPayload = ChargesPayload()
        chargesPayload.amount = binding.amountDueCharge.editableText.toString()
        chargesPayload.locale = binding.etChargeLocale.editableText.toString()
        chargesPayload.dueDate = dueDateString
        chargesPayload.dateFormat = "dd MMMM yyyy"
        chargesPayload.chargeId = chargeId
        initiateChargesCreation(chargesPayload)
    }

    override fun onDatePicked(date: String?) {
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
            .replace("-", " ")
        binding.chargeDueDate.text = dueDateString
    }

    //Charges Fetching API
    private fun inflateChargesSpinner() {
        viewModel.loadAllChargesV2(clientId)
    }

    //Charges Creation APi
    private fun initiateChargesCreation(chargesPayload: ChargesPayload) {
        viewModel.createCharges(clientId, chargesPayload)
    }

    private fun inflateDueDate() {
        mfDatePicker = newInsance(this)
        val receivedDate: String? = MFDatePicker.datePickedAsString
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(receivedDate)
            .replace("-", " ")
        dueDateAsIntegerList = DateHelper.convertDateAsListOfInteger(dueDateString)
        binding.chargeDueDate.text = dueDateString
    }

    private fun inflateDatePicker() {
        mfDatePicker?.show(
            requireActivity().supportFragmentManager,
            FragmentConstants.DFRAG_DATE_PICKER
        )
    }

    private fun inflateChargeNameSpinner() {
        chargeNameAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, chargeNameList
        )
        chargeNameAdapter
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spChargeName.adapter = chargeNameAdapter
        binding.spChargeName.onItemSelectedListener = this
    }

    private fun showAllChargesV2(chargeTemplate: ChargeTemplate) {
        mChargeTemplate = chargeTemplate
        viewModel.filterChargeName(chargeTemplate.chargeOptions).let { chargeNameList.addAll(it) }
        chargeNameAdapter.notifyDataSetChanged()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_charge_name -> {
                chargeId = mChargeTemplate?.chargeOptions?.getOrNull(position)?.id!!
                chargeName = mChargeTemplate?.chargeOptions?.getOrNull(position)?.name
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    private fun showChargesCreatedSuccessfully(chargeCreationResponse: ChargeCreationResponse) {
        if (chargeCreateListener != null) {
            createdCharge?.clientId = chargeCreationResponse.clientId
            createdCharge?.id = chargeCreationResponse.resourceId
            createdCharge?.let { chargeCreateListener?.onChargeCreatedSuccess(it) }
        } else {
            show(binding.root, getString(R.string.message_charge_created_success))
        }
        dialog?.dismiss()
    }

    private fun showChargeCreatedFailure(errorMessage: String) {
        if (chargeCreateListener != null) {
            chargeCreateListener?.onChargeCreatedFailure(errorMessage)
        } else {
            show(binding.root, errorMessage)
        }
    }

    private fun showFetchingError(s: String) {
        show(binding.root, s)
    }

    private fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    fun setOnChargeCreatedListener(chargeCreatedListener: OnChargeCreateListener?) {
        chargeCreateListener = chargeCreatedListener
    }

    companion object {
        fun newInstance(clientId: Int): ChargeDialogFragment {
            val chargeDialogFragment = ChargeDialogFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            chargeDialogFragment.arguments = args
            return chargeDialogFragment
        }
    }
}