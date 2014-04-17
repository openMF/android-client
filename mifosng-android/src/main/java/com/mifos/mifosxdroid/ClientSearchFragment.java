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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mifos.objects.client.PageItem;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mifos.utils.services.API;
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

    @InjectView(R.id.actv_clientSearch) AutoCompleteTextView actv_clientSearch;
    @InjectView(R.id.tv_fullName) TextView tv_fullName;
    @InjectView(R.id.tv_accountNumber) TextView tv_accountNumber;
    @InjectView(R.id.tv_externalId) TextView tv_externalId;
    @InjectView(R.id.tv_activationDate) TextView tv_activationDate;
    @InjectView(R.id.tv_office) TextView tv_office;
    @InjectView(R.id.tv_group) TextView tv_group;
    @InjectView(R.id.tv_loanOfficer) TextView tv_loanOfficer;
    @InjectView(R.id.tv_loanCycle) TextView tv_loanCycle;

    @InjectView(R.id.lv_searchResults) ListView lv_searchResults;

    @InjectView(R.id.bt_searchClient) Button bt_showClientDetails;

    ActionBarActivity activity;

    List<String> clientNames = new ArrayList<String>();
    List<Integer> clientIds = new ArrayList<Integer>();

    SafeUIBlockingUtility safeUIBlockingUtility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_client_search, container, false);
        activity = (ActionBarActivity) getActivity();

        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @OnClick(R.id.bt_searchClient)
    public void showClientDetails(Button btn){
        if(!actv_clientSearch.getEditableText().toString().isEmpty())
        {
            findClients(actv_clientSearch.getEditableText().toString());
        }
    }


    public void findClients(String clientName) {

        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientSearchFragment.this.getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        API.searchService.searchClientsByName(clientName,new Callback<List<SearchedEntity>>() {
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
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                performUICleanUp();
                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });




    }


    public void getClientInfo(int clientId){

        safeUIBlockingUtility.safelyBlockUI();
        API.clientService.getClient(clientId, new Callback<PageItem>() {
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
                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Client not found.", Toast.LENGTH_SHORT).show();
                performUICleanUp();
                safeUIBlockingUtility.safelyUnBlockUI();

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
