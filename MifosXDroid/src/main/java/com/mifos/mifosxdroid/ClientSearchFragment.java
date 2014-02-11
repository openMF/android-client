package com.mifos.mifosxdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.objects.PageItem;
import com.mifos.objects.User;
import com.mifos.utils.ClientService;
import com.mifos.utils.MifosRestAdapter;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 10/02/14.
 */
public class ClientSearchFragment extends Fragment {

    public ClientSearchFragment() {
    }


    private View rootView;

    private AutoCompleteTextView actv_clientSearch;
    private TextView tv_fullName;
    private TextView tv_accountNumber;
    private TextView tv_externalId;
    private TextView tv_activationDate;
    private TextView tv_office;
    private TextView tv_group;
    private TextView tv_loanOfficer;
    private TextView tv_loanCycle;

    private Button bt_showClientDetails;

    SharedPreferences sharedPreferences;

    ActionBarActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_client_details, container, false);
        activity = (ActionBarActivity) getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        setupUI();


        return rootView;
    }

    public void setupUI() {

        actv_clientSearch = (AutoCompleteTextView) rootView.findViewById(R.id.actv_clientSearch);
        //TODO Add Client names to this box

        tv_fullName = (TextView) rootView.findViewById(R.id.tv_fullName);
        tv_accountNumber = (TextView) rootView.findViewById(R.id.tv_accountNumber);
        tv_externalId = (TextView) rootView.findViewById(R.id.tv_externalId);
        tv_activationDate = (TextView) rootView.findViewById(R.id.tv_activationDate);
        tv_office = (TextView) rootView.findViewById(R.id.tv_office);
        tv_group = (TextView) rootView.findViewById(R.id.tv_group);
        tv_loanOfficer = (TextView) rootView.findViewById(R.id.tv_loanOfficer);
        tv_loanCycle = (TextView) rootView.findViewById(R.id.tv_loanCycle);

        bt_showClientDetails = (Button) rootView.findViewById(R.id.bt_searchClient);
        bt_showClientDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchClient();

            }
        });

    }

    public void searchClient() {

        MifosRestAdapter mifosRestAdapter = new MifosRestAdapter(sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA"));

        ClientService clientService = mifosRestAdapter.getRestAdapter().create(ClientService.class);

        clientService.getClient(Integer.parseInt(actv_clientSearch.getEditableText().toString()), new Callback<PageItem>() {
            @Override
            public void success(PageItem pageItem, Response response) {

                if (pageItem != null) {
                    tv_fullName.setText(pageItem.getDisplayName());
                    tv_accountNumber.setText(pageItem.getAccountNo());
                    tv_externalId.setText(pageItem.getExternalId());
                    tv_activationDate.setText(pageItem.getActivationDate().toString());
                    tv_office.setText(pageItem.getOfficeName());

                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Client not found.", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
