/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.datatabledata;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.datatablerowdialog.DataTableRowDialogFragment;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.DataTableUIBuilder;
import com.mifos.utils.FragmentConstants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DataTableDataFragment extends MifosBaseFragment
        implements DataTableUIBuilder.DataTableActionListener, DataTableDataMvpView,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.linear_layout_datatables)
    LinearLayout linearLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressbar_data_table)
    ProgressBar pbDataTable;

    @BindView(R.id.ll_error)
    LinearLayout llError;

    @BindView(R.id.tv_error)
    TextView tvError;

    @Inject
    DataTableDataPresenter mDataTableDataPresenter;

    private DataTable dataTable;
    private int entityId;
    private View rootView;


    public static DataTableDataFragment newInstance(DataTable dataTable, int entityId) {
        DataTableDataFragment fragment = new DataTableDataFragment();
        fragment.dataTable = dataTable;
        fragment.entityId = entityId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_datatable, container, false);

        ButterKnife.bind(this, rootView);
        mDataTableDataPresenter.attachView(this);

        setToolbarTitle(dataTable.getRegisteredTableName());
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);

        mDataTableDataPresenter.loadDataTableInfo(dataTable.getRegisteredTableName(), entityId);

        return rootView;
    }

    @Override
    public void onRefresh() {
        linearLayout.setVisibility(View.GONE);
        mDataTableDataPresenter.loadDataTableInfo(dataTable.getRegisteredTableName(), entityId);
    }

    @Override
    public void showDataTableOptions(final String table, final int entity, final int rowId) {
        new MaterialDialog.Builder().init(getActivity())
                .setItems(R.array.datatable_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mDataTableDataPresenter.deleteDataTableEntry(table, entity, rowId);
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
    public void showDataTableInfo(JsonArray jsonElements) {
        if (jsonElements != null) {
            linearLayout.setVisibility(View.VISIBLE);
            llError.setVisibility(View.GONE);
            linearLayout.removeAllViews();
            linearLayout.invalidate();
            DataTableUIBuilder.DataTableActionListener mListener =
                    (DataTableUIBuilder
                            .DataTableActionListener) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(FragmentConstants.FRAG_DATA_TABLE);
            linearLayout = new DataTableUIBuilder().getDataTableLayout(dataTable,
                    jsonElements, linearLayout, getActivity(), entityId, mListener);
        }
    }

    @Override
    public void showDataTableDeletedSuccessfully() {
        mDataTableDataPresenter.loadDataTableInfo(dataTable.getRegisteredTableName(), entityId);
    }

    @Override
    public void showEmptyDataTable() {
        linearLayout.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        tvError.setText(R.string.empty_data_table);
        Toaster.show(rootView, R.string.empty_data_table);
    }

    @Override
    public void showFetchingError(int message) {
        showFetchingError(getString(message));
    }

    @Override
    public void showFetchingError(String errorMessage) {
        linearLayout.setVisibility(View.GONE);
        llError.setVisibility(View.VISIBLE);
        tvError.setText(errorMessage);
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(false);
        if (show) {
            linearLayout.setVisibility(View.GONE);
            pbDataTable.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            pbDataTable.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            DataTableRowDialogFragment dataTableRowDialogFragment = DataTableRowDialogFragment
                    .newInstance(dataTable, entityId);
            dataTableRowDialogFragment.setTargetFragment(this, Constants.DIALOG_FRAGMENT);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.DFRAG_DATATABLE_ENTRY_FORM);
            dataTableRowDialogFragment.show(fragmentTransaction, "Document Dialog Fragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    mDataTableDataPresenter
                            .loadDataTableInfo(dataTable.getRegisteredTableName(), entityId);
                }
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataTableDataPresenter.detachView();
    }
}
