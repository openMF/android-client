/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientSearchAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.SearchedEntity;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ClientSearchFragment extends MifosBaseFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = ClientSearchFragment.class.getSimpleName();

    @InjectView(R.id.et_search_by_id)
    EditText et_searchById;

    @InjectView(R.id.lv_searchResults)
    ListView results;

    private List<SearchedEntity> clients = new ArrayList<>();
    private ClientSearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_client_search, null);
        ButterKnife.inject(this, rootView);
        setToolbarTitle(getResources().getString(R.string.dashboard));
        adapter = new ClientSearchAdapter(getContext(), clients, R.layout.list_item_client);
        results.setAdapter(adapter);
        results.setOnItemClickListener(this);
        return rootView;
    }

    @OnClick(R.id.bt_searchClient)
    public void performSearch() {
        String q = et_searchById.getEditableText().toString().trim();
        if (!q.isEmpty())
            findClients(q);
        else
            Toaster.show(et_searchById, "No Search Query Entered!");
    }

    public void findClients(final String name) {
        EspressoIdlingResource.increment(); // App is busy until further notice.
        showProgress();

        App.apiManager.searchClientsByName(name, new Callback<List<SearchedEntity>>() {
            @Override
            public void success(List<SearchedEntity> result, Response response) {
                clients = result;
                adapter.setList(result);
                adapter.notifyDataSetChanged();

                if (result.isEmpty())
                    showAlertDialog("Message", "No results found for entered query");
                hideProgress();
                EspressoIdlingResource.decrement(); // App is idle.
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                EspressoIdlingResource.decrement(); // App is idle.
            }
        });
    }

    @Override
    public void onPause() {
        //Fragment getting detached, keyboard if open must be hidden
        hideKeyboard(et_searchById);
        super.onPause();
    }

    /**
     * There is a need for this method in the following cases :
     * <p/>
     * 1. If user entered a search query and went out of the app.
     * 2. If user entered a search query and got some search results and went out of the app.
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            String queryString = et_searchById.getEditableText().toString();
            if (queryString != null && !(queryString.equals(""))) {
                outState.putString(TAG + et_searchById.getId(), queryString);
            }
        } catch (NullPointerException npe) {
            //Looks like edit text didn't get initialized properly
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String queryString = savedInstanceState.getString(TAG + et_searchById.getId());
            if (!TextUtils.isEmpty(queryString)) {
                et_searchById.setText(queryString);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
        clientActivityIntent.putExtra(Constants.CLIENT_ID, clients.get(i).getEntityId());
        startActivity(clientActivityIntent);
    }
}