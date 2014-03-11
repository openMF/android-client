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

import com.mifos.mifosxdroid.adapters.AccountsListAdapter;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.client.PageItem;
import com.mifos.utils.MifosRestAdapter;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.utils.services.ClientAccountsService;
import com.mifos.utils.services.ClientService;

import java.util.Iterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ClientDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CLIENT_ID = "cliendId";

    public int clientId;


    private TextView tv_fullName;
    private TextView tv_accountNumber;
    private TextView tv_externalId;
    private TextView tv_activationDate;
    private TextView tv_office;
    private TextView tv_group;
    private TextView tv_loanOfficer;
    private TextView tv_loanCycle;

    private ListView lv_accounts;

    View rootView;

    MifosRestAdapter mifosRestAdapter;

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
        setupUI();
        getClientInfo(clientId);




        return rootView;
    }


    public void setupUI(){

        tv_fullName = (TextView) rootView.findViewById(R.id.tv_fullName);
        tv_accountNumber = (TextView) rootView.findViewById(R.id.tv_accountNumber);
        tv_externalId = (TextView) rootView.findViewById(R.id.tv_externalId);
        tv_activationDate = (TextView) rootView.findViewById(R.id.tv_activationDate);
        tv_office = (TextView) rootView.findViewById(R.id.tv_office);
        tv_group = (TextView) rootView.findViewById(R.id.tv_group);
        tv_loanOfficer = (TextView) rootView.findViewById(R.id.tv_loanOfficer);
        tv_loanCycle = (TextView) rootView.findViewById(R.id.tv_loanCycle);
        lv_accounts = (ListView) rootView.findViewById(R.id.lv_accounts);

    }

    public void getClientInfo(int clientId){

        mifosRestAdapter =
                new MifosRestAdapter(sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA"));

        ClientService clientService = mifosRestAdapter.getRestAdapter().create(ClientService.class);
        safeUIBlockingUtility.safelyBlockUI();
        clientService.getClient(clientId, new Callback<PageItem>() {
            @Override
            public void success(PageItem pageItem, Response response) {

                if (pageItem != null) {
                    actionBar.setTitle("Mifos Client - "+pageItem.getLastname());
                    tv_fullName.setText(pageItem.getDisplayName());
                    tv_accountNumber.setText(pageItem.getAccountNo());
                    tv_externalId.setText(pageItem.getExternalId());
                    tv_activationDate.setText(pageItem.getFormattedActivationDateAsString());
                    tv_office.setText(pageItem.getOfficeName());

                    ClientAccountsService clientAccountsService =
                            mifosRestAdapter.getRestAdapter().create(ClientAccountsService.class);

                    clientAccountsService.getAllAccountsOfClient(pageItem.getId(), new Callback<ClientAccounts>() {
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
