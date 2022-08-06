package com.mifos.mifosxdroid.online

import android.os.Bundle
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsFragment
import com.mifos.mifosxdroid.online.groupdetails.GroupDetailsFragment.Companion.newInstance
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryFragment
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentFragment
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleFragment
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsFragment
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionFragment
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.client.Client
import com.mifos.utils.Constants

/**
 * Created by nellyk on 2/27/2016.
 */
class GroupsActivity : MifosBaseActivity(), GroupDetailsFragment.OnFragmentInteractionListener, LoanAccountSummaryFragment.OnFragmentInteractionListener, LoanRepaymentFragment.OnFragmentInteractionListener, SavingsAccountSummaryFragment.OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        ButterKnife.bind(this)
        showBackButton()
        val groupId = intent.extras!!.getInt(Constants.GROUP_ID)
        replaceFragment(newInstance(groupId), false, R.id.container)
    }

    /**
     * Called when a Loan Account is Selected
     * from the list of Loan Accounts on Client Details Fragment
     * It displays the summary of the Selected Loan Account
     */
    override fun loadLoanAccountSummary(loanAccountNumber: Int) {
        replaceFragment(LoanAccountSummaryFragment.newInstance(loanAccountNumber, true), true,
                R.id.container)
    }

    /**
     * Called when a Savings Account is Selected
     * from the list of Savings Accounts on Client Details Fragment
     *
     *
     * It displays the summary of the Selected Savings Account
     */
    override fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType?) {
        replaceFragment(SavingsAccountSummaryFragment.newInstance(savingsAccountNumber,
                accountType, true), true, R.id.container)
    }

    /**
     * Called when the make the make repayment button is clicked
     * in the Loan Account Summary Fragment.
     *
     *
     * It will display the Loan Repayment Fragment where
     * the Information of the repayment has to be filled in.
     */
    override fun makeRepayment(loan: LoanWithAssociations?) {
        replaceFragment(LoanRepaymentFragment.newInstance(loan), true, R.id.container)
    }

    /**
     * Called when the Repayment Schedule option from the Menu is
     * clicked
     *
     *
     * It will display the Complete Loan Repayment Schedule.
     */
    override fun loadRepaymentSchedule(loanId: Int) {
        replaceFragment(LoanRepaymentScheduleFragment.newInstance(loanId), true, R.id.container)
    }

    /**
     * Called when the Transactions option from the Menu is clicked
     *
     *
     * It will display all the Transactions associated with the Loan
     * and also their details
     */
    override fun loadLoanTransactions(loanId: Int) {
        replaceFragment(LoanTransactionsFragment.newInstance(loanId), true, R.id.container)
    }

    /**
     * Called when the make the make deposit button is clicked
     * in the Savings Account Summary Fragment.
     *
     *
     * It will display the Transaction Fragment where the information
     * of the transaction has to be filled in.
     *
     *
     * The transactionType defines if the transaction is a Deposit or a Withdrawal
     */
    override fun doTransaction(savingsAccountWithAssociations: SavingsAccountWithAssociations?, transactionType: String?, accountType: DepositType?) {
        replaceFragment(SavingsAccountTransactionFragment.newInstance(savingsAccountWithAssociations!!, transactionType, accountType), true, R.id.container)
    }

    override fun loadGroupClients(clients: List<Client>?) {
        replaceFragment(ClientListFragment.newInstance(clients, true), true, R.id.container)
    }
}