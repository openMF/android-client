/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.loan.SavingsApproval
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.utils.*
import javax.inject.Inject

/**
 * @author nellyk
 */
class SavingsAccountApprovalFragment : MifosBaseFragment(), OnDatePickListener, SavingsAccountApprovalMvpView {
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.tv_approval_date)
    var tvApprovalDate: TextView? = null

    @JvmField
    @BindView(R.id.btn_approve_savings)
    var btnApproveSavings: Button? = null

    @JvmField
    @BindView(R.id.et_savings_approval_reason)
    var etSavingsApprovalReason: EditText? = null

    @JvmField
    @Inject
    var mSavingsAccountApprovalPresenter: SavingsAccountApprovalPresenter? = null
    lateinit var rootView: View
    var approvaldate: String? = null
    var savingsAccountNumber = 0
    var savingsAccountType: DepositType? = null
    private var mfDatePicker: DialogFragment? = null
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            savingsAccountNumber = requireArguments().getInt(Constants.SAVINGS_ACCOUNT_NUMBER)
            savingsAccountType = requireArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        rootView = inflater.inflate(R.layout.dialog_fragment_approve_savings, null)
        ButterKnife.bind(this, rootView)
        mSavingsAccountApprovalPresenter!!.attachView(this)
        safeUIBlockingUtility = SafeUIBlockingUtility(activity,
                getString(R.string.savings_account_approval_fragment_loading_message))
        showUserInterface()
        return rootView
    }

    override fun showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this)
        tvApprovalDate!!.text = MFDatePicker.getDatePickedAsString()
        approvaldate = tvApprovalDate!!.text.toString()
        showApprovalDate()
    }

    @OnClick(R.id.btn_approve_savings)
    fun onClickApproveSavings() {
        if (Network.isOnline(context)) {
            val savingsApproval = SavingsApproval()
            savingsApproval.note = etSavingsApprovalReason!!.editableText.toString()
            savingsApproval.approvedOnDate = approvaldate
            initiateSavingsApproval(savingsApproval)
        } else {
            Toast.makeText(context,
                    resources.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    @OnClick(R.id.tv_approval_date)
    fun onClickApprovalDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String) {
        tvApprovalDate!!.text = date
        approvaldate = date
        showApprovalDate()
    }

    fun showApprovalDate() {
        approvaldate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(approvaldate)
                .replace("-", " ")
    }

    private fun initiateSavingsApproval(savingsApproval: SavingsApproval) {
        mSavingsAccountApprovalPresenter!!.approveSavingsApplication(
                savingsAccountNumber, savingsApproval)
    }

    override fun showSavingAccountApprovedSuccessfully(genericResponse: GenericResponse?) {
        Toast.makeText(activity, "Savings Approved", Toast.LENGTH_LONG).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility!!.safelyBlockUI()
        } else {
            safeUIBlockingUtility!!.safelyUnBlockUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSavingsAccountApprovalPresenter!!.detachView()
    }

    companion object {
        fun newInstance(savingsAccountNumber: Int,
                        type: DepositType?): SavingsAccountApprovalFragment {
            val savingsAccountApproval = SavingsAccountApprovalFragment()
            val args = Bundle()
            args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber)
            args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type)
            savingsAccountApproval.arguments = args
            return savingsAccountApproval
        }
    }
}