package com.mifos.mifosxdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.objects.PageItem;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.utils.ClientService;
import com.mifos.utils.MifosRestAdapter;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.utils.SearchService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 10/02/14.
 */
public class ClientSearchFragment extends Fragment implements AdapterView.OnItemClickListener{

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

    private ListView lv_searchResults;

    private Button bt_showClientDetails;

    SharedPreferences sharedPreferences;

    ActionBarActivity activity;

    List<String> clientNames = new ArrayList<String>();
    List<Integer> clientIds = new ArrayList<Integer>();

    MifosRestAdapter mifosRestAdapter;

    SafeUIBlockingUtility safeUIBlockingUtility;

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

        lv_searchResults = (ListView) rootView.findViewById(R.id.lv_searchResults);

        bt_showClientDetails = (Button) rootView.findViewById(R.id.bt_searchClient);
        bt_showClientDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!actv_clientSearch.getEditableText().toString().isEmpty())
                {
                    findClients(actv_clientSearch.getEditableText().toString());
                }

            }
        });

    }


    public void findClients(String clientName) {

        mifosRestAdapter =
                new MifosRestAdapter(sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA"));

        SearchService searchService = mifosRestAdapter.getRestAdapter().create(SearchService.class);

        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientSearchFragment.this.getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        searchService.searchClientsByName(clientName,new Callback<List<SearchedEntity>>() {
            @Override
            public void success(List<SearchedEntity> searchedEntities, Response response) {

                Iterator<SearchedEntity> iterator = searchedEntities.iterator();
                while(iterator.hasNext())
                {
                    SearchedEntity searchedEntity = iterator.next();
                    clientNames.add(searchedEntity.getEntityName());
                    clientIds.add(searchedEntity.getEntityId());
                }

                String[] clientNamesArrayForAdapter = clientNames.toArray(new String[clientNames.size()]);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClientSearchFragment.this.getActivity(),
                        android.R.layout.simple_list_item_1,clientNamesArrayForAdapter);

                lv_searchResults.setAdapter(adapter);
                lv_searchResults.setOnItemClickListener(ClientSearchFragment.this);
                performUICleanUp();
                safeUIBlockingUtility.safeUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                performUICleanUp();
                safeUIBlockingUtility.safeUnBlockUI();
            }
        });




    }


    public void getClientInfo(int clientId){

        ClientService clientService = mifosRestAdapter.getRestAdapter().create(ClientService.class);
        safeUIBlockingUtility.safelyBlockUI();
        clientService.getClient(clientId, new Callback<PageItem>() {
            @Override
            public void success(PageItem pageItem, Response response) {

                if (pageItem != null) {
                    tv_fullName.setText(pageItem.getDisplayName());
                    tv_accountNumber.setText(pageItem.getAccountNo());
                    tv_externalId.setText(pageItem.getExternalId());
                    tv_activationDate.setText(pageItem.getActivationDate().toString());
                    tv_office.setText(pageItem.getOfficeName());

                }
                performUICleanUp();
                safeUIBlockingUtility.safeUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Client not found.", Toast.LENGTH_SHORT).show();
                performUICleanUp();
                safeUIBlockingUtility.safeUnBlockUI();

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getClientInfo(clientIds.get(i));
    }

    public void performUICleanUp(){

        clientNames.clear();
        actv_clientSearch.setText("");
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actv_clientSearch.getWindowToken(), 0);
    }

}
