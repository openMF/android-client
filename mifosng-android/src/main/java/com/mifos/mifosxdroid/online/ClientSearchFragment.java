package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.utils.services.API;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientSearchFragment extends Fragment implements AdapterView.OnItemClickListener {


    @InjectView(R.id.et_search_by_id) EditText et_searchById;
    @InjectView(R.id.bt_searchClient) Button bt_searchClient;
    @InjectView(R.id.lv_searchResults) ListView lv_searchResults;
    View rootView;
    private OnFragmentInteractionListener mListener;

    private String searchQuery;

    List<String> clientNames = new ArrayList<String>();
    List<Integer> clientIds = new ArrayList<Integer>();

    SafeUIBlockingUtility safeUIBlockingUtility;


    public static ClientSearchFragment newInstance() {
        ClientSearchFragment fragment = new ClientSearchFragment();
        return fragment;
    }
    public ClientSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_search,null);
        ButterKnife.inject(this, rootView);



        return rootView;
    }

    @OnClick(R.id.bt_searchClient)
    public void performSearch(){

        if(!et_searchById.getEditableText().toString().trim().isEmpty())
        {
            searchQuery = et_searchById.getEditableText().toString().trim();
            findClients(searchQuery);

        }else
        {
            Toast.makeText(getActivity(),"No Search Query Entered!",Toast.LENGTH_SHORT).show();
        }


    }


    public void findClients(String clientName) {

        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
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

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,clientNamesArrayForAdapter);

                lv_searchResults.setAdapter(adapter);
                lv_searchResults.setOnItemClickListener(ClientSearchFragment.this);
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
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
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        mListener.loadClientDetails(clientIds.get(i));

    }


    public interface OnFragmentInteractionListener {

        public void loadClientDetails(int clientId);

    }

}
