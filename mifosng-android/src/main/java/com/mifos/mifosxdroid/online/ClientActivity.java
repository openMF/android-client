package com.mifos.mifosxdroid.online;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.Loan;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import butterknife.ButterKnife;

public class ClientActivity extends ActionBarActivity implements ClientDetailsFragment.OnFragmentInteractionListener,
                                                                 LoanAccountSummaryFragment.OnFragmentInteractionListener,
                                                                 LoanRepaymentFragment.OnFragmentInteractionListener,
                                                                 SavingsAccountSummaryFragment.OnFragmentInteractionListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_container_layout);
        ButterKnife.inject(this);
        final int clientId = getIntent().getExtras().getInt(Constants.CLIENT_ID);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        ClientDetailsFragment clientDetailsFragment = ClientDetailsFragment.newInstance(clientId);
        fragmentTransaction.replace(R.id.global_container, clientDetailsFragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadLoanAccountSummary(int loanAccountNumber) {

        LoanAccountSummaryFragment loanAccountSummaryFragment
                = LoanAccountSummaryFragment.newInstance(loanAccountNumber);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container,loanAccountSummaryFragment).commit();

    }

    @Override
    public void loadSavingsAccountSummary(int savingsAccountNumber) {
        SavingsAccountSummaryFragment savingsAccountSummaryFragment
                = SavingsAccountSummaryFragment.newInstance(savingsAccountNumber);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container,savingsAccountSummaryFragment).commit();
    }


    @Override
    public void makeRepayment(Loan loan) {

        LoanRepaymentFragment loanRepaymentFragment = LoanRepaymentFragment.newInstance(loan);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_LOAN_ACCOUNT_SUMMARY);
        fragmentTransaction.replace(R.id.global_container, loanRepaymentFragment).commit();
    }

    @Override
    public void makeDeposit() {

    }

    @Override
    public void makeWithdrawal() {

    }
}
