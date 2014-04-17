package com.mifos.mifosxdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.adapters.AccountsListAdapter;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.client.PageItem;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.Iterator;

import com.mifos.utils.services.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ClientDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CLIENT_ID = "cliendId";

    public int clientId;

    @InjectView(R.id.tv_fullName) TextView tv_fullName;
    @InjectView(R.id.tv_accountNumber) TextView tv_accountNumber;
    @InjectView(R.id.tv_externalId) TextView tv_externalId;
    @InjectView(R.id.tv_activationDate) TextView tv_activationDate;
    @InjectView(R.id.tv_office) TextView tv_office;
    @InjectView(R.id.tv_group) TextView tv_group;
    @InjectView(R.id.tv_loanOfficer) TextView tv_loanOfficer;
    @InjectView(R.id.tv_loanCycle) TextView tv_loanCycle;

    @InjectView(R.id.lv_accounts) ListView lv_accounts;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param clientId Client's Id
     * @return A new instance of fragment ClientDetailsFragment.
     */
    public static ClientDetailsFragment newInstance(int clientId) {
        ClientDetailsFragment fragment = new ClientDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }
    public ClientDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clientId = getArguments().getInt(CLIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_client_details, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientDetailsFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);
        getClientInfo(clientId);

        return rootView;
    }


    public void getClientInfo(int clientId){

        safeUIBlockingUtility.safelyBlockUI();
        API.clientService.getClient(clientId, new Callback<PageItem>() {
            @Override
            public void success(PageItem pageItem, Response response) {

                if (pageItem != null) {
                    actionBar.setTitle("Mifos Client - "+pageItem.getLastname());
                    tv_fullName.setText(pageItem.getDisplayName());
                    tv_accountNumber.setText(pageItem.getAccountNo());
                    tv_externalId.setText(pageItem.getExternalId());
                    tv_activationDate.setText(pageItem.getFormattedActivationDateAsString());
                    tv_office.setText(pageItem.getOfficeName());

                    API.clientAccountsService.getAllAccountsOfClient(pageItem.getId(), new Callback<ClientAccounts>() {
                        @Override
                        public void success(ClientAccounts clientAccounts, Response response) {


                            AccountsListAdapter accountsListAdapter =
                                    new AccountsListAdapter(ClientDetailsFragment.this.getActivity(),clientAccounts.getLoanAccounts());
                            //TODO REMOVE THIS PRINTER
                            try{
                                Iterator<LoanAccount> iterator = clientAccounts.getLoanAccounts().iterator();
                                while(iterator.hasNext())
                                {
                                    LoanAccount loanAccount = iterator.next();
                                    System.out.println(loanAccount.toString());
                                }
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            lv_accounts.setAdapter(accountsListAdapter);

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {

                            Toast.makeText(activity, "Accounts not found.", Toast.LENGTH_SHORT).show();

                        }
                    });
                    safeUIBlockingUtility.safelyUnBlockUI();

                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Client not found.", Toast.LENGTH_SHORT).show();
                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });

    }



    @Override
    public void onDetach() {
        super.onDetach();
    }



}
