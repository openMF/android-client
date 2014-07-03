package com.mifos.mifosxdroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.CenterDetailsActivity;
import com.mifos.mifosxdroid.ClientActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.MifosGroupListAdapter;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.MifosGroup;
import com.mifos.services.API;
import com.mifos.services.RepaymentTransactionSyncService;
import com.mifos.services.data.Payload;
import com.mifos.utils.Network;
import com.orm.query.Select;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements AdapterView.OnItemClickListener, RepaymentTransactionSyncService.SyncFinishListener {

    private final List<MifosGroup> groupList = new ArrayList<MifosGroup>();
    @InjectView(R.id.lv_group)
    ListView lv_group;
    @InjectView(R.id.progress_group)
    ProgressBar progressGroup;
    MifosGroupListAdapter adapter = null;
    String tag = getClass().getSimpleName();
    View view;
    @InjectView(R.id.tv_empty_group)
    TextView tv_empty_group;
    private MenuItem syncItem;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, null);
        setHasOptionsMenu(true);
        ButterKnife.inject(this, view);
        getData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_sync, menu);
        syncItem = menu.findItem(R.id.action_sync);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sync) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View syncProgress = inflater.inflate(R.layout.sync_progress, null);
            MenuItemCompat.setActionView(item, syncProgress);
            RepaymentTransactionSyncService syncService = new RepaymentTransactionSyncService(this);
            syncService.syncRepayments();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        groupList.clear();
        groupList.addAll(getAllGroups());
        if (adapter == null) {
            adapter = new MifosGroupListAdapter(getActivity(), groupList);
            lv_group.setAdapter(adapter);
        }
        lv_group.setOnItemClickListener(this);
        lv_group.setEmptyView(progressGroup);
        adapter.notifyDataSetChanged();
        if (groupList.size() == 0 && syncItem != null) {
            MenuItemCompat.setActionView(syncItem, null);
            SharedPreferences preferences = getActivity().getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
            date = preferences.getString(CenterDetailsActivity.TRANSACTION_DATE_KEY, null);
            int centerId = preferences.getInt(CenterDetailsActivity.CENTER_ID_KEY, -1);
            tv_empty_group.setVisibility(View.VISIBLE);
            tv_empty_group.setText("There is no data for center " + centerId + " on " + date);
            progressGroup.setVisibility(View.GONE);

        } else
            tv_empty_group.setVisibility(View.GONE);
    }

    private Payload getPayload() {
        final Payload payload = new Payload();
        SharedPreferences preferences = getActivity().getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
        date = preferences.getString(CenterDetailsActivity.TRANSACTION_DATE_KEY, null);
        if (date != null) {
            String[] splittedDate = date.split("-");
            int month = Integer.parseInt(splittedDate[1]);
            final StringBuilder builder = new StringBuilder();
            builder.append(splittedDate[0]);
            builder.append(" ");
            builder.append(getMonthName(month));
            builder.append(" ");
            builder.append(splittedDate[2]);
            payload.setTransactionDate(builder.toString());
        }
        return payload;

    }

    private String getMonthName(int month) {
        String monthName = "";
        switch (month) {
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
        }
        return monthName;
    }

    private void getData() {

        if (Network.isOnline(getActivity().getApplicationContext())) {
            SharedPreferences preferences = getActivity().getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
            int centerId = preferences.getInt(CenterDetailsActivity.CENTER_ID_KEY, -1);
            API.centerService.getCenter(centerId, getPayload(), new Callback<CollectionSheet>() {
                @Override
                public void success(CollectionSheet collectionSheet, Response arg1) {

                    if (collectionSheet != null) {
                        collectionSheet.saveData();

                        if (groupList.size() == 0)
                            setAdapter();
                    }
                }

                @Override
                public void failure(RetrofitError arg0) {
                    Toast.makeText(getActivity(), "There was some error fetching data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<MifosGroup> getAllGroups() {
        return Select.from(MifosGroup.class).list();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ClientActivity.class);
        intent.putExtra("group_id", groupList.get(i).getId());
        Log.i(tag, "onItemClick = Group ID:" + groupList.get(i).getId());
        startActivity(intent);
    }

    @Override
    public void onSyncFinish(String message, boolean isSyncable) {
        MenuItemCompat.setActionView(syncItem, null);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        if (!isSyncable) {
            SharedPreferences preferences = getActivity().getSharedPreferences(CenterDetailsActivity.PREF_CENTER_DETAILS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), CenterDetailsActivity.class);
            startActivity(intent);
        }
    }
}
