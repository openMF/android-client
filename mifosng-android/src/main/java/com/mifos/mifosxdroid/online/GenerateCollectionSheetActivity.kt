/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.mifos.api.model.IndividualCollectionSheetPayload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.online.collectionsheetindividual.IndividualCollectionSheetFragment
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.PaymentDetailsFragment.OnPayloadSelectedListener
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.mifosxdroid.online.generatecollectionsheet.GenerateCollectionSheetFragment
import com.mifos.mifosxdroid.online.loanaccountsummary.LoanAccountSummaryFragment
import com.mifos.mifosxdroid.online.loanrepayment.LoanRepaymentFragment
import com.mifos.mifosxdroid.online.loanrepaymentschedule.LoanRepaymentScheduleFragment
import com.mifos.mifosxdroid.online.loantransactions.LoanTransactionsFragment
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.utils.Constants

class GenerateCollectionSheetActivity : MifosBaseActivity(), OnPayloadSelectedListener, LoanAccountSummaryFragment.OnFragmentInteractionListener {
    var payload: IndividualCollectionSheetPayload? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        val fragmentToOpen: String
        if (intent != null) {
            fragmentToOpen = intent.getStringExtra(Constants.COLLECTION_TYPE)
            if (fragmentToOpen == Constants.EXTRA_COLLECTION_INDIVIDUAL) {
                replaceFragment(IndividualCollectionSheetFragment.newInstance(),
                        false, R.id.container)
            } else if (fragmentToOpen == Constants.EXTRA_COLLECTION_COLLECTION) {
                replaceFragment(GenerateCollectionSheetFragment.newInstance(),
                        false, R.id.container)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onPayloadSelected(payload: IndividualCollectionSheetPayload?) {
        this.payload = payload
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
}