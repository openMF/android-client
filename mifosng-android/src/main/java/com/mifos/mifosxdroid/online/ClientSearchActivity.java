package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;

import butterknife.ButterKnife;


public class ClientSearchActivity extends ActionBarActivity implements ClientSearchFragment.OnFragmentInteractionListener,
                                                                       LoanAccountSummaryFragment.OnFragmentInteractionListener,
                                                                       ClientDetailsFragment.OnFragmentInteractionListener{




    ActionBarActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_search);
        ButterKnife.inject(this);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        ClientSearchFragment clientSearchFragment = new ClientSearchFragment();
        fragmentTransaction.replace(R.id.search_activity_container,clientSearchFragment).commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_search, menu);
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
    public void loadClientDetails(int clientId) {
        ClientDetailsFragment clientDetailsFragment = ClientDetailsFragment.newInstance(clientId);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.search_activity_container,clientDetailsFragment).commit();
    }




    @Override
    public void onBackPressed(int clientId) {

        loadClientDetails(clientId);
    }

    @Override
    public void loadLoanAccountSummary(int loanAccountNumber) {

        LoanAccountSummaryFragment loanAccountSummaryFragment = LoanAccountSummaryFragment.newInstance(loanAccountNumber);
        FragmentTransaction fragmentTransaction =  getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("Client Details Fragment");
        fragmentTransaction.replace(R.id.search_activity_container,loanAccountSummaryFragment).commit();


    }
}
