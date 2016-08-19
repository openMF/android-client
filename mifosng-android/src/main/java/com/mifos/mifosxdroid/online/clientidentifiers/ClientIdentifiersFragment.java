/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientidentifiers;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.IdentifierListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.objects.noncore.Identifier;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mifos.mifosxdroid.adapters.IdentifierListAdapter.DeleteIdentifierListener;


public class ClientIdentifiersFragment extends ProgressableFragment
        implements ClientIdentifiersMvpView, DeleteIdentifierListener {

    @BindView(R.id.lv_identifiers)
    ListView lv_identifiers;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject
    ClientIdentifiersPresenter mClientIdentifiersPresenter;
    private View rootView;
    private int clientId;
    List<Identifier> identifiers;
    IdentifierListAdapter identifierListAdapter;

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
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_identifiers, container, false);

        ButterKnife.bind(this, rootView);
        mClientIdentifiersPresenter.attachView(this);

        setToolbarTitle(getString(R.string.identifiers));
        loadIdentifiers();

        swipeToRefresh();

        return rootView;
    }

    public void loadIdentifiers() {
        mClientIdentifiersPresenter.loadIdentifiers(clientId);
    }

    public void swipeToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadIdentifiers();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void showClientIdentifiers(List<Identifier> identifiers) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;
        this.identifiers = identifiers;

        if (identifiers != null && identifiers.size() > 0) {
            identifierListAdapter = new IdentifierListAdapter(getActivity(),
                    this.identifiers, clientId, this);
            lv_identifiers.setAdapter(identifierListAdapter);
        } else {
            Toast.makeText(getActivity(), getString(R.string
                            .message_no_identifiers_available),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickRemoveIdentifier(int identifierId, int position) {
        mClientIdentifiersPresenter.deleteIdentifier(clientId, identifierId, position);
    }

    @Override
    public void identifierDeletedSuccessfully(String s, int position) {
        identifiers.remove(position);
        identifierListAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientIdentifiersPresenter.detachView();
    }
}