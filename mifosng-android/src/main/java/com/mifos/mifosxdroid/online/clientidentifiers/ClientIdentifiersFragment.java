/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientidentifiers;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.IdentifierListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.identifierdialog.IdentifierDialogFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.objects.noncore.Identifier;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mifos.mifosxdroid.adapters.IdentifierListAdapter.IdentifierOptionsListener;


public class ClientIdentifiersFragment extends MifosBaseFragment implements
        ClientIdentifiersMvpView, IdentifierOptionsListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_client_identifier)
    RecyclerView rv_client_identifier;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noIdentifierText)
    TextView mNoIdentifierText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @BindView(R.id.noIdentifierIcon)
    ImageView mNoIdentifierIcon;

    @Inject
    ClientIdentifiersPresenter mClientIdentifiersPresenter;

    @Inject
    IdentifierListAdapter identifierListAdapter;

    private View rootView;
    private int clientId;
    List<Identifier> identifiers;
    private LinearLayoutManager mLayoutManager;

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
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_identifiers, container, false);

        ButterKnife.bind(this, rootView);
        mClientIdentifiersPresenter.attachView(this);

        showUserInterface();
        loadIdentifiers();

        return rootView;
    }

    public void loadIdentifiers() {
        mClientIdentifiersPresenter.loadIdentifiers(clientId);
    }

    @Override
    public void onRefresh() {
        loadIdentifiers();
    }

    @Override
    public void showUserInterface() {
        setToolbarTitle(getString(R.string.identifiers));
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        identifierListAdapter.setIdentifierOptionsListener(this);
        rv_client_identifier.setLayoutManager(mLayoutManager);
        rv_client_identifier.setHasFixedSize(true);
        rv_client_identifier.setAdapter(identifierListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showClientIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
        identifierListAdapter.setIdentifiers(identifiers);
    }

    @Override
    public void showEmptyClientIdentifier() {
        ll_error.setVisibility(View.VISIBLE);
        mNoIdentifierText.setText(getResources().getString(R.string.no_identifier_to_show));
        mNoIdentifierIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
    }

    @Override
    public void showFetchingError(int errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickIdentifierOptions(final int position, View view) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_client_identifier, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_remove_identifier:
                        mClientIdentifiersPresenter.deleteIdentifier(clientId,
                                identifiers.get(position).getId());
                        break;
                    case R.id.menu_identifier_documents:
                        DocumentListFragment documentListFragment =
                                DocumentListFragment.newInstance(
                                        Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
                                        identifiers.get(position).getId());
                        FragmentTransaction fragmentTransaction =
                                getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.addToBackStack(FragmentConstants
                                .FRAG_CLIENT_IDENTIFIER);
                        fragmentTransaction.replace(R.id.container, documentListFragment);
                        fragmentTransaction.commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void identifierDeletedSuccessfully() {
        Toast.makeText(getActivity(), R.string.identifier_deleted_successfully,
                Toast.LENGTH_SHORT).show();
        loadIdentifiers();
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && identifierListAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                IdentifierDialogFragment identifierDialogFragment =
                        IdentifierDialogFragment.newInstance(clientId);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST);
                identifierDialogFragment.show(fragmentTransaction,
                        getString(R.string.identifier_dialog_fragment));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientIdentifiersPresenter.detachView();
        hideMifosProgressBar();
    }
}