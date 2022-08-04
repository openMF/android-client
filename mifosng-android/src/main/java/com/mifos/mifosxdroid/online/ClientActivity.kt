/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.content.Intent
import android.os.Bundle
import butterknife.ButterKnife
import com.google.gson.Gson
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.clientdetails.ClientDetailsFragment
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryFragment
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentFragment
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleFragment
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsFragment
import com.mifos.mifosxdroid.online.savingaccountsummary.SavingsAccountSummaryFragment
import com.mifos.mifosxdroid.online.savingaccounttransaction.SavingsAccountTransactionFragment
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.survey.Survey
import com.mifos.utils.Constants

class ClientActivity : MifosBaseActivity(), ClientDetailsFragment.OnFragmentInteractionListener, LoanAccountSummaryFragment.OnFragmentInteractionListener, SavingsAccountSummaryFragment.OnFragmentInteractionListener, SurveyListFragment.OnFragmentInteractionListener {
    private var clientId = 0
    private var loanAccountNumber = 0
    private var savingsAccountNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        ButterKnife.bind(this)
        showBackButton()
        intent.extras?.let { extras ->
            clientId = extras.getInt(Constants.CLIENT_ID)
            loanAccountNumber = extras.getInt(Constants.LOAN_ACCOUNT_NUMBER)
            savingsAccountNumber = extras.getInt(Constants.SAVINGS_ACCOUNT_NUMBER)   
        }
        val depositType = DepositType()
        depositType.id = 100
        depositType.value = Constants.ENTITY_TYPE_SAVINGS
        if (clientId != 0) {
            replaceFragment(ClientDetailsFragment.Companion.newInstance(clientId), false, R.id.container)
        } else if (loanAccountNumber != 0) {
            replaceFragment(LoanAccountSummaryFragment.newInstance(loanAccountNumber, false), true,
                    R.id.container)
        } else if (savingsAccountNumber != 0) {
            replaceFragment(SavingsAccountSummaryFragment.newInstance(savingsAccountNumber,
                    depositType, false), true, R.id.container)
        }
    }

    /**
     * Called when a Loan Account is Selected
     * from the list of Loan Accounts on Client Details Fragment
     * It displays the summary of the Selected Loan Account
     */
    override fun loadLoanAccountSummary(loanAccountNumber: Int) {
        replaceFragment(LoanAccountSummaryFragment.newInstance(loanAccountNumber, true), true, R.id.container)
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

    /**
     * Called when the Survey Question Card Click and Survey Question View opens for making survey.
     *
     * @param survey   Survey
     * @param clientId Client Id
     */
    override fun loadSurveyQuestion(survey: Survey?, clientId: Int) {
        val myIntent = Intent(this, SurveyQuestionActivity::class.java)
        myIntent.putExtra(Constants.SURVEYS, Gson().toJson(survey))
        myIntent.putExtra(Constants.CLIENT_ID, clientId)
        startActivity(myIntent)
    }
}