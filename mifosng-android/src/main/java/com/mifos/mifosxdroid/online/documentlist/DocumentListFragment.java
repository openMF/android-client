/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.documentlist;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.DocumentListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment;
import com.mifos.objects.noncore.Document;
import com.mifos.utils.AsyncFileDownloader;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentListFragment extends MifosBaseFragment implements DocumentListMvpView,
        RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int MENU_ITEM_ADD_NEW_DOCUMENT = 1000;

    @BindView(R.id.rv_documents)
    RecyclerView rv_documents;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noDocumentText)
    TextView mNoChargesText;

    @BindView(R.id.noDocumentIcon)
    ImageView mNoChargesIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    DocumentListPresenter mDocumentListPresenter;

    @Inject
    DocumentListAdapter mDocumentListAdapter;

    private View rootView;
    private String entityType;
    private int entityId;
    private List<Document> mDocumentList;

    public static DocumentListFragment newInstance(String entityType, int entiyId) {
        DocumentListFragment fragment = new DocumentListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ENTITY_TYPE, entityType);
        args.putInt(Constants.ENTITY_ID, entiyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(View childView, int position) {
        AsyncFileDownloader asyncFileDownloader =
                new AsyncFileDownloader(getActivity(), mDocumentList.get(position).getFileName());
        asyncFileDownloader.execute(entityType, String.valueOf(entityId),
                String.valueOf(mDocumentList.get(position).getId()));
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mDocumentList = new ArrayList<>();
        if (getArguments() != null) {
            entityType = getArguments().getString(Constants.ENTITY_TYPE);
            entityId = getArguments().getInt(Constants.ENTITY_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_document_list, container, false);

        ButterKnife.bind(this, rootView);
        mDocumentListPresenter.attachView(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_documents.setLayoutManager(layoutManager);
        rv_documents.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_documents.setHasFixedSize(true);
        rv_documents.setAdapter(mDocumentListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);

        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        return rootView;
    }

    @Override
    public void onRefresh() {
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
    }

    @OnClick(R.id.noDocumentIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
    }

    @Override
    public void showDocumentList(final List<Document> documents) {
        mDocumentList = documents;
        mDocumentListAdapter.setDocuments(mDocumentList);
    }

    @Override
    public void showEmptyDocuments() {
        ll_error.setVisibility(View.VISIBLE);
        mNoChargesText.setText(getResources().getString(R.string.no_document_to_show));
        mNoChargesIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
    }

    @Override
    public void showFetchingError(int message) {
        if (mDocumentListAdapter.getItemCount() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            String errorMessage = getStringMessage(message) + getStringMessage(R.string.new_line) +
                    getStringMessage(R.string.click_to_refresh);
            mNoChargesText.setText(errorMessage);
        } else {
            Toaster.show(rootView, getStringMessage(message));
        }

    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mDocumentListAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDocumentListPresenter.detachView();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_DOCUMENT, Menu
                .NONE, getString(R.string.add_new));
        menuItemAddNewDocument
                .setIcon(ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_action_content_new));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_DOCUMENT) {
            DocumentDialogFragment documentDialogFragment = DocumentDialogFragment.newInstance
                    (entityType, entityId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST);
            documentDialogFragment.show(fragmentTransaction, "Document Dialog Fragment");
        }
        return super.onOptionsItemSelected(item);
    }
}
