/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.google.gson.Gson
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityToolbarContainerBinding
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

class ClientActivity : MifosBaseActivity(),
    SurveyListFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityToolbarContainerBinding
    private lateinit var navHostFragment : NavHostFragment
    private var clientId: Int? = null
    private var loanAccountNumber: Int? = null
    private var savingsAccountNumber: Int? = null

    private val args: ClientActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolbarContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()
        navHostFragment = supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        clientId = args.client.clientId
        loanAccountNumber = args.client.loanAccountNumber
        savingsAccountNumber = args.client.savingsAccountNumber
        val depositType = DepositType()
        depositType.id = 100
        depositType.value = Constants.ENTITY_TYPE_SAVINGS
        if (clientId != 0) {
            val bundle = Bundle()
            clientId?.let { bundle.putInt(Constants.CLIENT_ID, it) }
            navHostFragment.navController.apply {
                popBackStack()
                navigate(R.id.clientDetailsFragment, bundle)
            }
        } else if (loanAccountNumber != 0) {
            val bundle = Bundle()
            loanAccountNumber?.let { bundle.putInt(Constants.LOAN_ACCOUNT_NUMBER, it) }
            findNavController(R.id.container_nav_host_fragment).apply {
                popBackStack()
                navigate(R.id.loanAccountSummaryFragment, bundle)
            }
        } else if (savingsAccountNumber != 0) {
            val bundle = Bundle()
            savingsAccountNumber?.let { bundle.putInt(Constants.SAVINGS_ACCOUNT_ID, it) }
            findNavController(R.id.container_nav_host_fragment).apply {
                popBackStack()
                navigate(R.id.clientDetailsFragment, bundle)
            }
        }
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