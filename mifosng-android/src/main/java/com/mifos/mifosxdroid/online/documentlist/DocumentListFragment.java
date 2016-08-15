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
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
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

public class DocumentListFragment extends MifosBaseFragment implements DocumentListMvpView,
        RecyclerItemClickListener.OnItemClickListener {

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


        mDocumentListPresenter.loadDocumentList(entityType, entityId);

        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDocumentListPresenter.loadDocumentList(entityType, entityId);

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_documents.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                //Future Implementation
            }
        });

        return rootView;
    }

    @Override
    public void showDocumentList(final List<Document> documents) {

        if (documents.size() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            mNoChargesText.setText("There is No Documents to Show");
            mNoChargesIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mDocumentList = documents;
            mDocumentListAdapter = new DocumentListAdapter(getActivity(), documents);
            rv_documents.setAdapter(mDocumentListAdapter);
        }
    }

    @Override
    public void showFetchingError(String s) {
        ll_error.setVisibility(View.VISIBLE);
        mNoChargesText.setText(s + "\n Click to Refresh ");
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressBar();
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
