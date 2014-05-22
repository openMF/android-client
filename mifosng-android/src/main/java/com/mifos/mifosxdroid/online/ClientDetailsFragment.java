package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.AccountsListAdapter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.services.API;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ClientDetailsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    
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
        args.putInt(Constants.CLIENT_ID, clientId);
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
            clientId = getArguments().getInt(Constants.CLIENT_ID);
            System.out.print(clientId);
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
        API.clientService.getClient(clientId, new Callback<Client>() {
            @Override
            public void success(Client client, Response response) {

                if (client != null) {
                    actionBar.setTitle("Mifos Client - " + client.getLastname());
                    tv_fullName.setText(client.getDisplayName());
                    tv_accountNumber.setText(client.getAccountNo());
                    tv_externalId.setText(client.getExternalId());
                    tv_activationDate.setText(client.getFormattedActivationDateAsString());
                    tv_office.setText(client.getOfficeName());

                    API.clientAccountsService.getAllAccountsOfClient(client.getId(), new Callback<ClientAccounts>() {
                        @Override
                        public void success(final ClientAccounts clientAccounts, Response response) {


                            AccountsListAdapter accountsListAdapter =
                                    new AccountsListAdapter(getActivity().getApplicationContext(),clientAccounts.getLoanAccounts());

                            lv_accounts.setAdapter(accountsListAdapter);
                            lv_accounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    mListener.loadLoanAccountSummary(clientAccounts.getLoanAccounts().get(i).getId());



                                }
                            });
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {

        public void loadLoanAccountSummary(int loanAccountNumber);

    }


}
