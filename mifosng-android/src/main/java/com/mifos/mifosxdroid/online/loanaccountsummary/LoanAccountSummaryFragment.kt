/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.loanaccountsummary

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.QuickContactBadge
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableFragment
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
import java.util.*
import javax.inject.Inject

/**
 * Created by ishankhanna on 09/05/14.
 */
class LoanAccountSummaryFragment : ProgressableFragment(), LoanAccountSummaryMvpView {
    var loanAccountNumber = 0

    @kotlin.jvm.JvmField
    @BindView(R.id.view_status_indicator)
    var view_status_indicator: View? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_clientName)
    var tv_clientName: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.quickContactBadge_client)
    var quickContactBadge: QuickContactBadge? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_product_short_name)
    var tv_loan_product_short_name: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loanAccountNumber)
    var tv_loanAccountNumber: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_amount_disbursed)
    var tv_amount_disbursed: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_disbursement_date)
    var tv_disbursement_date: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_in_arrears)
    var tv_in_arrears: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_officer)
    var tv_loan_officer: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_principal)
    var tv_principal: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_principal_due)
    var tv_loan_principal_due: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_principal_paid)
    var tv_loan_principal_paid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_interest)
    var tv_interest: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_interest_due)
    var tv_loan_interest_due: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_interest_paid)
    var tv_loan_interest_paid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_fees)
    var tv_fees: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_fees_due)
    var tv_loan_fees_due: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_fees_paid)
    var tv_loan_fees_paid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_penalty)
    var tv_penalty: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_penalty_due)
    var tv_loan_penalty_due: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_loan_penalty_paid)
    var tv_loan_penalty_paid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total)
    var tv_total: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_due)
    var tv_total_due: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.tv_total_paid)
    var tv_total_paid: TextView? = null

    @kotlin.jvm.JvmField
    @BindView(R.id.bt_processLoanTransaction)
    var bt_processLoanTransaction: Button? = null

    @kotlin.jvm.JvmField
    @Inject
    var mLoanAccountSummaryPresenter: LoanAccountSummaryPresenter? = null
    var chargesList: MutableList<Charges> = ArrayList()
    private lateinit var rootView: View

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
        rootView = inflater.inflate(R.layout.fragment_loan_account_summary, container, false)

        //Injecting Presenter
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        ButterKnife.bind(this, rootView)
        mLoanAccountSummaryPresenter!!.attachView(this)
        inflateLoanAccountSummary()
        return rootView
    }

    private fun inflateLoanAccountSummary() {
        showProgress(true)
        setToolbarTitle(resources.getString(R.string.loanAccountSummary))
        //TODO Implement cases to enable/disable repayment button
        bt_processLoanTransaction!!.isEnabled = false
        mLoanAccountSummaryPresenter!!.loadLoanById(loanAccountNumber)
    }

    @OnClick(R.id.bt_processLoanTransaction)
    fun onProcessTransactionClicked() {
        if (processLoanTransactionAction == TRANSACTION_REPAYMENT) {
            mListener!!.makeRepayment(clientLoanWithAssociations)
        } else if (processLoanTransactionAction == ACTION_APPROVE_LOAN) {
            approveLoan()
        } else if (processLoanTransactionAction == ACTION_DISBURSE_LOAN) {
            disburseLoan()
        } else {
            Log.i(requireActivity().localClassName, "TRANSACTION ACTION NOT SET")
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
        tv_amount_disbursed!!.text = loanWithAssociations.summary
                .principalDisbursed.toString()
        try {
            tv_disbursement_date!!.text = DateHelper.getDateAsString(loanWithAssociations
                    .timeline.actualDisbursementDate)
        } catch (exception: IndexOutOfBoundsException) {
            Toast.makeText(activity, resources.getString(R.string.loan_rejected_message), Toast.LENGTH_SHORT).show()
        }
        tv_in_arrears!!.text = loanWithAssociations.summary.totalOverdue.toString()
        tv_principal!!.text = loanWithAssociations.summary
                .principalDisbursed.toString()
        tv_loan_principal_due!!.text = loanWithAssociations.summary
                .principalOutstanding.toString()
        tv_loan_principal_paid!!.text = loanWithAssociations.summary
                .principalPaid.toString()
        tv_interest!!.text = loanWithAssociations.summary.interestCharged.toString()
        tv_loan_interest_due!!.text = loanWithAssociations.summary
                .interestOutstanding.toString()
        tv_loan_interest_paid!!.text = loanWithAssociations.summary
                .interestPaid.toString()
        tv_fees!!.text = loanWithAssociations.summary.feeChargesCharged.toString()
        tv_loan_fees_due!!.text = loanWithAssociations.summary
                .feeChargesOutstanding.toString()
        tv_loan_fees_paid!!.text = loanWithAssociations.summary
                .feeChargesPaid.toString()
        tv_penalty!!.text = loanWithAssociations.summary
                .penaltyChargesCharged.toString()
        tv_loan_penalty_due!!.text = loanWithAssociations.summary
                .penaltyChargesOutstanding.toString()
        tv_loan_penalty_paid!!.text = loanWithAssociations.summary
                .penaltyChargesPaid.toString()
        tv_total!!.text = loanWithAssociations.summary
                .totalExpectedRepayment.toString()
        tv_total_due!!.text = loanWithAssociations.summary.totalOutstanding.toString()
        tv_total_paid!!.text = loanWithAssociations.summary.totalRepayment.toString()
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
        tv_clientName!!.text = loanWithAssociations.clientName
        tv_loan_product_short_name!!.text = loanWithAssociations.loanProductName
        tv_loanAccountNumber!!.text = "#" + loanWithAssociations.accountNo
        tv_loan_officer!!.text = loanWithAssociations.loanOfficerName
        //TODO Implement QuickContactBadge
        //quickContactBadge.setImageToDefault();
        bt_processLoanTransaction!!.isEnabled = true
        if (loanWithAssociations.status.active) {
            inflateLoanSummary(loanWithAssociations)
            // if Loan is already active
            // the Transaction Would be Make Repayment
            view_status_indicator!!.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.light_green))
            bt_processLoanTransaction!!.text = "Make Repayment"
            processLoanTransactionAction = TRANSACTION_REPAYMENT
        } else if (loanWithAssociations.status.pendingApproval) {
            // if Loan is Pending for Approval
            // the Action would be Approve Loan
            view_status_indicator!!.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.light_yellow))
            bt_processLoanTransaction!!.text = "Approve Loan"
            processLoanTransactionAction = ACTION_APPROVE_LOAN
        } else if (loanWithAssociations.status.waitingForDisbursal) {
            // if Loan is Waiting for Disbursal
            // the Action would be Disburse Loan
            view_status_indicator!!.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.blue))
            bt_processLoanTransaction!!.text = "Disburse Loan"
            processLoanTransactionAction = ACTION_DISBURSE_LOAN
        } else if (loanWithAssociations.status.closedObligationsMet) {
            inflateLoanSummary(loanWithAssociations)
            // if Loan is Closed after the obligations are met
            // the make payment will be disabled so that no more payment can be collected
            view_status_indicator!!.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.black))
            bt_processLoanTransaction!!.isEnabled = false
            bt_processLoanTransaction!!.text = "Make Repayment"
        } else {
            inflateLoanSummary(loanWithAssociations)
            view_status_indicator!!.setBackgroundColor(
                    ContextCompat.getColor(requireActivity(), R.color.black))
            bt_processLoanTransaction!!.isEnabled = false
            bt_processLoanTransaction!!.text = "Loan Closed"
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
        @kotlin.jvm.JvmStatic
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