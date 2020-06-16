/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.documentlist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.documentdialog.DocumentDialogFragment;
import com.mifos.objects.noncore.Document;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.FileUtils;
import com.mifos.utils.FragmentConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class DocumentListFragment extends MifosBaseFragment implements DocumentListMvpView,
        RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int MENU_ITEM_ADD_NEW_DOCUMENT = 1000;

    public static final String LOG_TAG = DocumentListFragment.class.getSimpleName();

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
    private Document document;
    private ResponseBody documentBody;
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
        document = mDocumentList.get(position);
        showDocumentActions(mDocumentList.get(position).getId());
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

    @Override
    public void onResume() {
        super.onResume();
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
    }

    @OnClick(R.id.noDocumentIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
    }

    /**
     * This Method Checking the Permission WRITE_EXTERNAL_STORAGE is granted or not.
     * If not then prompt user a dialog to grant the WRITE_EXTERNAL_STORAGE permission.
     * and If Permission is granted already then Save the documentBody in external storage;
     */
    @Override
    public void checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            checkExternalStorageAndCreateDocument();
        } else {
            requestPermission();
        }
    }

    @Override
    public void requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
                (MifosBaseActivity) getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE,
                getResources().getString(
                        R.string.dialog_message_write_external_storage_permission_denied),
                getResources().getString(R.string.dialog_message_permission_never_ask_again_write),
                Constants.WRITE_EXTERNAL_STORAGE_STATUS);
    }

    /**
     * This Method getting the Response after User Grant or denied the Permission
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    checkExternalStorageAndCreateDocument();

                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(rootView, getResources()
                            .getString(R.string.permission_denied_to_write_external_document));
                }
            }
        }
    }


    @Override
    public void showDocumentList(final List<Document> documents) {
        mDocumentList = documents;
        mDocumentListAdapter.setDocuments(mDocumentList);
    }

    @Override
    public void showDocumentFetchSuccessfully(ResponseBody responseBody) {
        documentBody = responseBody;
        checkPermissionAndRequest();
    }

    @Override
    public void showDocumentActions(final int documentId) {
        new MaterialDialog.Builder().init(getActivity())
                .setItems(R.array.document_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mDocumentListPresenter.downloadDocument(entityType, entityId,
                                        documentId);
                                break;
                            case 1:
                                showDocumentDialog(getString(R.string.update_document));
                                break;
                            case 2:
                                mDocumentListPresenter.removeDocument(entityType, entityId,
                                        documentId);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .createMaterialDialog()
                .show();
    }

    @Override
    public void checkExternalStorageAndCreateDocument() {
        // Create a path where we will place our documents in the user's
        // public directory and check if the file exists.
        File mifosDirectory = new File(Environment.getExternalStorageDirectory(),
                getResources().getString(R.string.document_directory));
        if (!mifosDirectory.exists()) {
            mifosDirectory.mkdirs();
        }

        File documentFile = new File(mifosDirectory.getPath(), document.getFileName());
        FileUtils.writeInputStreamDataToFile(documentBody.byteStream(), documentFile);

        //Opening the Saved Document
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(documentFile),
                FileUtils.getMimeType(mifosDirectory.getPath() +
                        getResources().getString(R.string.slash) + document.getFileName()));
        startActivity(intent);
    }

    @Override
    public void showDocumentRemovedSuccessfully() {
        Toaster.show(rootView, getResources().getString(R.string.document_remove_successfully));
        mDocumentListPresenter.loadDocumentList(entityType, entityId);
    }

    @Override
    public void showDocumentDialog(String documentAction) {
        DocumentDialogFragment documentDialogFragment =
                DocumentDialogFragment.newInstance(entityType, entityId, documentAction, document);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST);
        documentDialogFragment.show(fragmentTransaction, "Document Dialog Fragment");
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
        hideMifosProgressBar();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_DOCUMENT, Menu
                .NONE, getString(R.string.add_new));
        menuItemAddNewDocument
                .setIcon(ContextCompat
                        .getDrawable(getActivity(), R.drawable.ic_add_white_24dp));

        menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_DOCUMENT) {
            showDocumentDialog(getString(R.string.upload_document));
        }
        return super.onOptionsItemSelected(item);
    }
}
