/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountapproval

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveSavingsBinding
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
    private lateinit var binding: DialogFragmentApproveSavingsBinding

    @JvmField
    @Inject
    var mSavingsAccountApprovalPresenter: SavingsAccountApprovalPresenter? = null
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

        binding = DialogFragmentApproveSavingsBinding.inflate(inflater,container,false)
        mSavingsAccountApprovalPresenter!!.attachView(this)
        safeUIBlockingUtility = SafeUIBlockingUtility(activity,
                getString(R.string.savings_account_approval_fragment_loading_message))
        showUserInterface()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApproveSavings.setOnClickListener { onClickApproveSavings() }
        binding.tvApprovalDate.setOnClickListener { onClickApprovalDate() }
    }

    override fun showUserInterface() {
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvApprovalDate.text = MFDatePicker.getDatePickedAsString()
        approvaldate = binding.tvApprovalDate.text.toString()
        showApprovalDate()
    }

    fun onClickApproveSavings() {
        if (Network.isOnline(context)) {
            val savingsApproval = SavingsApproval()
            savingsApproval.note = binding.etSavingsApprovalReason.editableText.toString()
            savingsApproval.approvedOnDate = approvaldate
            initiateSavingsApproval(savingsApproval)
        } else {
            Toast.makeText(context,
                    resources.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show()
        }
    }

    fun onClickApprovalDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String) {
        binding.tvApprovalDate.text = date
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