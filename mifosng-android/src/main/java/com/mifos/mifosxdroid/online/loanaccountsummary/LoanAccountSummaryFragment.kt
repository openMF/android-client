/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountsummary

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
import com.mifos.mifosxdroid.databinding.FragmentLoanAccountSummaryBinding
import com.mifos.mifosxdroid.online.datatable.DataTableFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.mifosxdroid.online.loanaccountapproval.LoanAccountApproval
import com.mifos.mifosxdroid.online.loanaccountdisbursement.LoanAccountDisbursementFragment
import com.mifos.mifosxdroid.online.loancharge.LoanChargeFragment
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.client.Charges
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.FragmentConstants
import javax.inject.Inject

/**
 * Created by ishankhanna on 09/05/14.
 */
class LoanAccountSummaryFragment : ProgressableFragment(), LoanAccountSummaryMvpView {
    var loanAccountNumber = 0
    private lateinit var binding: FragmentLoanAccountSummaryBinding

    @JvmField
    @Inject
    var mLoanAccountSummaryPresenter: LoanAccountSummaryPresenter? = null
    var chargesList: MutableList<Charges> = ArrayList()

    // Action Identifier in the onProcessTransactionClicked Method
    private var processLoanTransactionAction = -1
    private var parentFragment = true
    private var mListener: OnFragmentInteractionListener? = null
    private var clientLoanWithAssociations: LoanWithAssociations? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            loanAccountNumber = requireArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER)
            parentFragment = requireArguments().getBoolean(Constants.IS_A_PARENT_FRAGMENT)
        }
        //Necessary Call to add and update the Menu in a Fragment
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoanAccountSummaryBinding.inflate(inflater,container,false)

        //Injecting Presenter
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        mLoanAccountSummaryPresenter!!.attachView(this)
        inflateLoanAccountSummary()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btProcessLoanTransaction.setOnClickListener { onProcessTransactionClicked() }
    }

    private fun inflateLoanAccountSummary() {
        showProgress(true)
        setToolbarTitle(resources.getString(R.string.loanAccountSummary))
        //TODO Implement cases to enable/disable repayment button
        binding.btProcessLoanTransaction.isEnabled = false
        mLoanAccountSummaryPresenter!!.loadLoanById(loanAccountNumber)
    }

    fun onProcessTransactionClicked() {
        when (processLoanTransactionAction) {
            TRANSACTION_REPAYMENT -> {
                mListener!!.makeRepayment(clientLoanWithAssociations)
            }
            ACTION_APPROVE_LOAN -> {
                approveLoan()
            }
            ACTION_DISBURSE_LOAN -> {
                disburseLoan()
            }
            else -> {
                Log.i(requireActivity().localClassName, "TRANSACTION ACTION NOT SET")
            }
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (!parentFragment) {
            requireActivity().finish()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_LOAN_NAME)
        menu.add(Menu.NONE, MENU_ITEM_LOAN_TRANSACTIONS, Menu.NONE, resources.getString(R.string.transactions))
        menu.add(Menu.NONE, MENU_ITEM_REPAYMENT_SCHEDULE, Menu.NONE, resources.getString(R.string.loan_repayment_schedule))
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, resources.getString(R.string.documents))
        menu.add(Menu.NONE, MENU_ITEM_CHARGES, Menu.NONE, resources.getString(R.string.charges))
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            MENU_ITEM_REPAYMENT_SCHEDULE -> mListener!!.loadRepaymentSchedule(loanAccountNumber)
            MENU_ITEM_LOAN_TRANSACTIONS -> mListener!!.loadLoanTransactions(loanAccountNumber)
            MENU_ITEM_DOCUMENTS -> loadDocuments()
            MENU_ITEM_CHARGES -> loadloanCharges()
            MENU_ITEM_DATA_TABLES -> loadLoanDataTables()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun inflateLoanSummary(loanWithAssociations: LoanWithAssociations) {
        binding.tvAmountDisbursed.text = loanWithAssociations.summary
                .principalDisbursed.toString()
        try {
            binding.tvDisbursementDate.text = DateHelper.getDateAsString(loanWithAssociations
                    .timeline.actualDisbursementDate)
        } catch (exception: IndexOutOfBoundsException) {
            Toast.makeText(activity, resources.getString(R.string.loan_rejected_message), Toast.LENGTH_SHORT).show()
        }
        binding.tvInArrears.text = loanWithAssociations.summary.totalOverdue.toString()
        binding.tvPrincipal.text = loanWithAssociations.summary
                .principalDisbursed.toString()
        binding.tvLoanPrincipalDue.text = loanWithAssociations.summary
                .principalOutstanding.toString()
        binding.tvLoanPrincipalPaid.text = loanWithAssociations.summary
                .principalPaid.toString()
        binding.tvInterest.text = loanWithAssociations.summary.interestCharged.toString()
        binding.tvLoanInterestDue.text = loanWithAssociations.summary
                .interestOutstanding.toString()
        binding.tvLoanInterestPaid.text = loanWithAssociations.summary
                .interestPaid.toString()
        binding.tvFees.text = loanWithAssociations.summary.feeChargesCharged.toString()
        binding.tvLoanFeesDue.text = loanWithAssociations.summary
                .feeChargesOutstanding.toString()
        binding.tvLoanFeesPaid.text = loanWithAssociations.summary
                .feeChargesPaid.toString()
        binding.tvPenalty.text = loanWithAssociations.summary
                .penaltyChargesCharged.toString()
        binding.tvLoanPenaltyDue.text = loanWithAssociations.summary
                .penaltyChargesOutstanding.toString()
        binding.tvLoanPenaltyPaid.text = loanWithAssociations.summary
                .penaltyChargesPaid.toString()
        binding.tvTotal.text = loanWithAssociations.summary
                .totalExpectedRepayment.toString()
        binding.tvTotalDue.text = loanWithAssociations.summary.totalOutstanding.toString()
        binding.tvTotalPaid.text = loanWithAssociations.summary.totalRepayment.toString()
    }

    fun loadDocuments() {
        val documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_LOANS, loanAccountNumber)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, documentListFragment)
        fragmentTransaction.commit()
    }

    fun loadloanCharges() {
        val loanChargeFragment: LoanChargeFragment = LoanChargeFragment.Companion.newInstance(loanAccountNumber,
                chargesList)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, loanChargeFragment)
        fragmentTransaction.commit()
    }

    fun approveLoan() {
        val loanAccountApproval: LoanAccountApproval = LoanAccountApproval.Companion.newInstance(loanAccountNumber, clientLoanWithAssociations)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, loanAccountApproval)
        fragmentTransaction.commit()
    }

    fun disburseLoan() {
        val loanAccountDisbursement: LoanAccountDisbursementFragment = LoanAccountDisbursementFragment.Companion.newInstance(loanAccountNumber)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, loanAccountDisbursement)
        fragmentTransaction.commit()
    }

    fun loadLoanDataTables() {
        val loanAccountFragment = DataTableFragment.newInstance(Constants.DATA_TABLE_NAME_LOANS, loanAccountNumber)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY)
        fragmentTransaction.replace(R.id.container, loanAccountFragment)
        fragmentTransaction.commit()
    }

    override fun showLoanById(loanWithAssociations: LoanWithAssociations) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (activity == null) return
        clientLoanWithAssociations = loanWithAssociations
        binding.tvClientName.text = loanWithAssociations.clientName
        binding.tvLoanProductShortName.text = loanWithAssociations.loanProductName
        binding.tvLoanAccountNumber.text = "#" + loanWithAssociations.accountNo
        binding.tvLoanOfficer.text = loanWithAssociations.loanOfficerName
        //TODO Implement QuickContactBadge
        //quickContactBadge.setImageToDefault();
        binding.btProcessLoanTransaction.isEnabled = true
        if (loanWithAssociations.status.active) {
            inflateLoanSummary(loanWithAssociations)
            // if Loan is already active
            // the Transaction Would be Make Repayment
            binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.light_green))
            binding.btProcessLoanTransaction.text = "Make Repayment"
            processLoanTransactionAction = TRANSACTION_REPAYMENT
        } else if (loanWithAssociations.status.pendingApproval) {
            // if Loan is Pending for Approval
            // the Action would be Approve Loan
            binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.light_yellow))
            binding.btProcessLoanTransaction.text = "Approve Loan"
            processLoanTransactionAction = ACTION_APPROVE_LOAN
        } else if (loanWithAssociations.status.waitingForDisbursal) {
            // if Loan is Waiting for Disbursal
            // the Action would be Disburse Loan
            binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.blue))
            binding.btProcessLoanTransaction.text = "Disburse Loan"
            processLoanTransactionAction = ACTION_DISBURSE_LOAN
        } else if (loanWithAssociations.status.closedObligationsMet) {
            inflateLoanSummary(loanWithAssociations)
            // if Loan is Closed after the obligations are met
            // the make payment will be disabled so that no more payment can be collected
            binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.black))
            binding.btProcessLoanTransaction.isEnabled = false
            binding.btProcessLoanTransaction.text = "Make Repayment"
        } else {
            inflateLoanSummary(loanWithAssociations)
            binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.black))
            binding.btProcessLoanTransaction.isEnabled = false
            binding.btProcessLoanTransaction.text = "Loan Closed"
        }
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(b: Boolean) {
        showProgress(b)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLoanAccountSummaryPresenter!!.detachView()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("LoanWithAssociation", clientLoanWithAssociations)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            clientLoanWithAssociations = savedInstanceState.getParcelable("LoanWithAssociation")
        }
    }

    interface OnFragmentInteractionListener {
        fun makeRepayment(loan: LoanWithAssociations?)
        fun loadRepaymentSchedule(loanId: Int)
        fun loadLoanTransactions(loanId: Int)
    }

    companion object {
        const val MENU_ITEM_DATA_TABLES = 1001
        const val MENU_ITEM_REPAYMENT_SCHEDULE = 1002
        const val MENU_ITEM_LOAN_TRANSACTIONS = 1003
        const val MENU_ITEM_DOCUMENTS = 1004
        const val MENU_ITEM_CHARGES = 1005

        /*
        Set of Actions and Transactions that can be performed depending on the status of the Loan
        Actions are performed to change the status of the loan
        Transactions are performed to do repayments
     */
        private const val ACTION_NOT_SET = -1
        private const val ACTION_APPROVE_LOAN = 0
        private const val ACTION_DISBURSE_LOAN = 1
        private const val TRANSACTION_REPAYMENT = 2
        @JvmStatic
        fun newInstance(loanAccountNumber: Int,
                        parentFragment: Boolean): LoanAccountSummaryFragment {
            val fragment = LoanAccountSummaryFragment()
            val args = Bundle()
            args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber)
            args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, parentFragment)
            fragment.arguments = args
            return fragment
        }
    }
}