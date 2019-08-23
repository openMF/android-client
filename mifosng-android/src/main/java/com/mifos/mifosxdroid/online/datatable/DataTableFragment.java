package com.mifos.mifosxdroid.online.datatable;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.DataTableAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 12/02/17.
 */

public class DataTableFragment extends MifosBaseFragment implements DataTableMvpView,
        SwipeRefreshLayout.OnRefreshListener, RecyclerItemClickListener.OnItemClickListener {

    @BindView(R.id.rv_data_table)
    RecyclerView rvDataTable;

    @BindView(R.id.progressbar_data_table)
    ProgressBar pbDataTable;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.iv_error)
    ImageView ivError;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    DataTablePresenter dataTablePresenter;

    @Inject
    DataTableAdapter dataTableAdapter;

    View rootView;

    private String tableName;
    private int entityId;
    private List<DataTable> dataTables;

    @Override
    public void onItemClick(View childView, int position) {
        DataTableDataFragment dataTableDataFragment
                = DataTableDataFragment.newInstance(dataTables.get(position), entityId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, dataTableDataFragment, FragmentConstants
                .FRAG_DATA_TABLE);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    public static DataTableFragment newInstance(String tableName, int entityId) {
        Bundle arguments = new Bundle();
        DataTableFragment dataTableFragment = new DataTableFragment();
        arguments.putString(Constants.DATA_TABLE_NAME, tableName);
        arguments.putInt(Constants.ENTITY_ID, entityId);
        dataTableFragment.setArguments(arguments);
        return dataTableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableName = getArguments().getString(Constants.DATA_TABLE_NAME);
            entityId = getArguments().getInt(Constants.ENTITY_ID);
        }
        dataTables = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        rootView = inflater.inflate(R.layout.fragment_datatables, container, false);
        ButterKnife.bind(this, rootView);
        dataTablePresenter.attachView(this);

        showUserInterface();
        dataTablePresenter.loadDataTable(tableName);

        return rootView;
    }

    @Override
    public void onRefresh() {
        dataTablePresenter.loadDataTable(tableName);
    }

    @Override
    public void showUserInterface() {
        setToolbarTitle(getResources().getString(R.string.datatables));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDataTable.setLayoutManager(mLayoutManager);
        rvDataTable.setHasFixedSize(true);
        rvDataTable.setAdapter(dataTableAdapter);
        rvDataTable.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showDataTables(List<DataTable> dataTables) {
        this.dataTables = dataTables;
        dataTableAdapter.setDataTables(dataTables);
    }

    @Override
    public void showEmptyDataTables() {
        ll_error.setVisibility(View.VISIBLE);
        rvDataTable.setVisibility(View.GONE);
        tvError.setText(R.string.empty_data_table);
    }

    @Override
    public void showResetVisibility() {
        ll_error.setVisibility(View.GONE);
        rvDataTable.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(int message) {
        Toaster.show(rootView, message);
        ll_error.setVisibility(View.VISIBLE);
        rvDataTable.setVisibility(View.GONE);
        tvError.setText( getString(R.string.failed_to_fetch_datatable));
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show && (dataTableAdapter.getItemCount() == 0)) {
            pbDataTable.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            pbDataTable.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(show);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataTablePresenter.detachView();
    }
}
