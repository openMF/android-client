/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountactivate

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
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentApproveSavingsBinding
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import com.mifos.utils.SafeUIBlockingUtility
import javax.inject.Inject

/**
 * Created by Tarun on 01/06/17.
 * Fragment to allow user to select a date for account approval.
 * It uses the same layout as Savings Account Approve Fragment.
 */
class SavingsAccountActivateFragment : MifosBaseFragment(), OnDatePickListener, SavingsAccountActivateMvpView {
    val LOG_TAG = javaClass.simpleName

    private lateinit var binding:DialogFragmentApproveSavingsBinding

    @JvmField
    @Inject
    var mSavingsAccountActivatePresenter: SavingsAccountActivatePresenter? = null
    var activationDate: String? = null
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
        binding = DialogFragmentApproveSavingsBinding.inflate(layoutInflater,container,false)

        mSavingsAccountActivatePresenter!!.attachView(this)
        safeUIBlockingUtility = SafeUIBlockingUtility(activity,
                getString(R.string.savings_account_loading_message))
        showUserInterface()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApproveSavings.setOnClickListener { onClickActivateSavings() }
        binding.tvApprovalDate.setOnClickListener { onClickApprovalDate() }
    }

    override fun showUserInterface() {
        binding.etSavingsApprovalReason.visibility = View.GONE
        binding.tvApprovalDateOn.text = resources.getString(R.string.activated_on)
        mfDatePicker = MFDatePicker.newInsance(this)
        binding.tvApprovalDate.text = MFDatePicker.getDatePickedAsString()
        activationDate = binding.tvApprovalDate.text.toString()
        showActivationDate()
    }

    fun onClickActivateSavings() {
        val hashMap = HashMap<String, Any?>()
        hashMap["dateFormat"] = "dd MMMM yyyy"
        hashMap["activatedOnDate"] = activationDate
        hashMap["locale"] = "en"
        mSavingsAccountActivatePresenter!!.activateSavings(savingsAccountNumber, hashMap)
    }

    fun onClickApprovalDate() {
        mfDatePicker!!.show(requireActivity().supportFragmentManager, FragmentConstants.DFRAG_DATE_PICKER)
    }

    override fun onDatePicked(date: String) {
        binding.tvApprovalDate.text = date
        activationDate = date
        showActivationDate()
    }

    fun showActivationDate() {
        activationDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationDate)
                .replace("-", " ")
    }

    override fun showSavingAccountActivatedSuccessfully(genericResponse: GenericResponse?) {
        Toaster.show(binding.tvApprovalDateOn,
                resources.getString(R.string.savings_account_activated))
        Toast.makeText(activity, "Savings Activated", Toast.LENGTH_LONG).show()
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
        mSavingsAccountActivatePresenter!!.detachView()
    }

    companion object {
        fun newInstance(savingsAccountNumber: Int,
                        type: DepositType?): SavingsAccountActivateFragment {
            val savingsAccountApproval = SavingsAccountActivateFragment()
            val args = Bundle()
            args.putInt(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountNumber)
            args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, type)
            savingsAccountApproval.arguments = args
            return savingsAccountApproval
        }
    }
}