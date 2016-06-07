/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.datatabledata;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.dialogfragments.DataTableRowDialogFragment;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.DataTableUIBuilder;
import com.mifos.utils.FragmentConstants;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DataTableDataFragment extends ProgressableFragment
        implements DataTableUIBuilder.DataTableActionListener, DataTableDataMvpView {

    public static final int MEUN_ITEM_ADD_NEW_ENTRY = 1000;

    @InjectView(R.id.linear_layout_datatables)
    LinearLayout linearLayout;

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
        ((MifosBaseActivity)getActivity()).getActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_datatable, container, false);

        ButterKnife.inject(this, rootView);
        mDataTableDataPresenter.attachView(this);

        setToolbarTitle(dataTable.getRegisteredTableName());

        inflateView();

        return rootView;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewEntryToDataTable = menu.add(Menu.NONE, MEUN_ITEM_ADD_NEW_ENTRY,
                Menu.NONE, getString(R.string.add_new));
        menuItemAddNewEntryToDataTable.setIcon(getResources().getDrawable(R.drawable
                .ic_action_content_new));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menuItemAddNewEntryToDataTable.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MEUN_ITEM_ADD_NEW_ENTRY) {
            DataTableRowDialogFragment dataTableRowDialogFragment = DataTableRowDialogFragment
                    .newInstance(dataTable, entityId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.DFRAG_DATATABLE_ENTRY_FORM);
            dataTableRowDialogFragment.show(fragmentTransaction, "Document Dialog Fragment");
        }

        return super.onOptionsItemSelected(item);
    }

    public void inflateView() {
        mDataTableDataPresenter.loadDataTableInfo(dataTable.getRegisteredTableName(), entityId);
    }

    @Override
    public void onUpdateActionRequested(JsonElement jsonElement) {

    }

    @Override
    public void onRowDeleted() {
        inflateView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataTableDataPresenter.detachView();
    }

    @Override
    public void showDataTableInfo(JsonArray jsonElements) {

        if (jsonElements != null) {
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
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }
}
