/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientIdentifiersfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.IdentifierListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.noncore.Identifier;
import com.mifos.utils.Constants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ClientIdentifiersFragment extends MifosBaseFragment implements ClientIdentifiersMvpView{

    @InjectView(R.id.lv_identifiers)
    ListView lv_identifiers;

    private View rootView;
    private int clientId;
    public DataManager mDataManager;
    public ClientIdentifiersPresenter mClientIdentifiersPresenter;

    public static ClientIdentifiersFragment newInstance(int clientId) {
        ClientIdentifiersFragment fragment = new ClientIdentifiersFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_identifiers, container, false);
        ButterKnife.inject(this, rootView);
        setToolbarTitle(getString(R.string.identifiers));
        mDataManager = new DataManager();
        mClientIdentifiersPresenter = new ClientIdentifiersPresenter(mDataManager);
        mClientIdentifiersPresenter.attachView(this);
        mClientIdentifiersPresenter.loadIdentifiers(clientId);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientIdentifiersPresenter.detachView();
    }

    @Override
    public void showIdentifiers(List<Identifier> identifiers) {
        if (identifiers != null && identifiers.size() > 0) {
            IdentifierListAdapter identifierListAdapter = new IdentifierListAdapter(getActivity(), identifiers, clientId);
            lv_identifiers.setAdapter(identifierListAdapter);
        } else {
            Toast.makeText(getActivity(), getString(R.string.message_no_identifiers_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showIdentifierFetchError() {
        Toaster.show(rootView , "Failed to Fetch Identifier");
    }

    @Override
    public void showIdentifierProgressBar(boolean status) {
        if(status){
            showProgress();
        }else {
            hideProgress();
        }
    }
}
