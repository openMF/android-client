/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.dialogfragments.chargedialog.OnChargeCreateListener
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.Companion.newInsance
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.client.Charges
import com.mifos.services.data.ChargesPayload
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
class LoanChargeDialogFragment : ProgressableDialogFragment(), OnDatePickListener,
    LoanChargeDialogMvpView {
    val LOG_TAG = javaClass.simpleName
    private lateinit var rootView: View

    @JvmField
    @BindView(R.id.sp_charge_name)
    var spChargeName: Spinner? = null

    @JvmField
    @BindView(R.id.amount_due_charge)
    var etAmountDue: EditText? = null

    @JvmField
    @BindView(R.id.et_date)
    var etChargeDueDate: EditText? = null

    @JvmField
    @BindView(R.id.et_charge_locale)
    var etChargeLocale: EditText? = null

    @JvmField
    @BindView(R.id.bt_save_charge)
    var btnSaveCharge: Button? = null

    @JvmField
    @Inject
    var mLoanChargeDialogPresenter: LoanChargeDialogPresenter? = null
    private var dueDateString: String? = null
    private var mfDatePicker: DialogFragment? = null
    private var chargeId = 0
    private var loanAccountNumber = 0
    private val chargeNameIdHashMap = HashMap<String, Int>()
    private var chargeName: String? = null
    private var createdCharge: Charges? = null
    private var dueDateAsIntegerList: List<Int>? = null
    private var onChargeCreateListener: OnChargeCreateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments != null) loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        if (requireActivity().actionBar != null) requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(true)
        rootView = inflater.inflate(R.layout.dialog_fragment_charge, null)
        ButterKnife.bind(this, rootView)
        mLoanChargeDialogPresenter!!.attachView(this)
        inflateDueDate()
        inflateChargesSpinner()
        return rootView
    }

    @OnClick(R.id.bt_save_charge)
    fun createCharge() {
        if (etAmountDue!!.text.toString().isEmpty()) {
            show(
                rootView, getString(R.string.amount)
                        + " " + getString(R.string.error_cannot_be_empty)
            )
            return
        }
        createdCharge = Charges()
        createdCharge!!.id = chargeId
        createdCharge!!.amount = etAmountDue!!.editableText.toString().toDouble()
        createdCharge!!.name = chargeName
        dueDateAsIntegerList = DateHelper.convertDateAsReverseInteger(dueDateString!!)
        createdCharge!!.dueDate = dueDateAsIntegerList
        val chargesPayload = ChargesPayload()
        chargesPayload.amount = etAmountDue!!.editableText.toString()
        chargesPayload.locale = etChargeLocale!!.editableText.toString()
        chargesPayload.dueDate = dueDateString
        chargesPayload.dateFormat = "dd MMMM yyyy"
        chargesPayload.chargeId = chargeId
        initiateChargesCreation(chargesPayload)
    }

    override fun onDatePicked(date: String?) {
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date)
            .replace("-", " ")
        etChargeDueDate!!.setText(dueDateString)
    }

    private fun inflateChargesSpinner() {
        mLoanChargeDialogPresenter!!.loanAllChargesV3(loanAccountNumber)
    }

    private fun initiateChargesCreation(chargesPayload: ChargesPayload) {
        mLoanChargeDialogPresenter!!.createLoanCharges(loanAccountNumber, chargesPayload)
    }

    private fun inflateDueDate() {
        mfDatePicker = newInsance(this)
        val receivedDate: String? = MFDatePicker.datePickedAsString
        dueDateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(receivedDate)
            .replace("-", " ")
        dueDateAsIntegerList = DateHelper.convertDateAsListOfInteger(dueDateString!!)
        etChargeDueDate!!.setText(dueDateString)
    }

    @OnClick(R.id.et_date)
    fun inflateDatePicker() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun showAllChargesV3(result: ResponseBody) {

        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        Log.d(LOG_TAG, "")
        val charges: MutableList<Charges> = ArrayList()
        // you can use this array to populate your spinner
        val chargesNames = ArrayList<String>()
        //Try to get response body
        var reader: BufferedReader? = null
        val sb = StringBuilder()
        try {
            reader = BufferedReader(InputStreamReader(result.byteStream()))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val obj = JSONObject(sb.toString())
            if (obj.has("chargeOptions")) {
                val chargesTypes = obj.getJSONArray("chargeOptions")
                for (i in 0 until chargesTypes.length()) {
                    val chargesObject = chargesTypes.getJSONObject(i)
                    val charge = Charges()
                    charge.id = chargesObject.optInt("id")
                    charge.name = chargesObject.optString("name")
                    charges.add(charge)
                    chargesNames.add(chargesObject.optString("name"))
                    chargeNameIdHashMap[charge.name] = charge.id
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "", e)
        }
        val chargesAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, chargesNames
        )
        chargesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spChargeName!!.adapter = chargesAdapter
        spChargeName!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                chargeId = chargeNameIdHashMap[chargesNames[i]]!!
                chargeName = chargesNames[i]
                Log.d("chargesoptionss" + chargesNames[i], chargeId.toString())
                if (chargeId == -1) {
                    Toast.makeText(
                        activity, getString(R.string.error_select_charge), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun showLoanChargesCreatedSuccessfully(chargeCreationResponse: ChargeCreationResponse) {
        if (onChargeCreateListener != null) {
            createdCharge!!.clientId = chargeCreationResponse.clientId
            createdCharge!!.id = chargeCreationResponse.resourceId
            onChargeCreateListener!!.onChargeCreatedSuccess(createdCharge)
        } else {
            show(rootView, getString(R.string.message_charge_created_success))
        }
        dialog!!.dismiss()
    }

    override fun showError(s: String) {
        show(rootView, s)
    }

    override fun showChargeCreatedFailure(errorMessage: String) {
        if (onChargeCreateListener != null) {
            onChargeCreateListener!!.onChargeCreatedFailure(errorMessage)
        } else {
            show(rootView, errorMessage)
        }
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanChargeDialogPresenter!!.detachView()
    }

    fun setOnChargeCreateListener(onChargeCreateListener: OnChargeCreateListener?) {
        this.onChargeCreateListener = onChargeCreateListener
    }

    companion object {
        fun newInstance(loanAccountNumber: Int): LoanChargeDialogFragment {
            val loanChargeDialogFragment = LoanChargeDialogFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            loanChargeDialogFragment.arguments = args
            return loanChargeDialogFragment
        }
    }
}