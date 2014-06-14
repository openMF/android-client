package com.mifos.mifosxdroid.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mifos.mifosxdroid.ClientActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.MifosGroup;
import com.mifos.services.API;
import com.mifos.services.RepaymentTransactionSyncService;
import com.mifos.services.data.Payload;
import com.mifos.utils.Network;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.lv_group)
    ListView lv_group;
    @InjectView(R.id.progress_group)
    ProgressBar progressGroup;

    GroupListAdapter adapter = null;
    private final List<MifosGroup> groupList = new ArrayList<MifosGroup>();
    String tag = getClass().getSimpleName();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, null);
        ButterKnife.inject(this, view);
        getData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
        getActivity().startService(new Intent(RepaymentTransactionSyncService.class.getName()));
    }

    private void setAdapter() {
        groupList.clear();
        groupList.addAll(getAllGroups());
        if (adapter == null) {
            adapter = new GroupListAdapter(getActivity(), groupList);
            lv_group.setAdapter(adapter);
        }
        lv_group.setOnItemClickListener(this);
        lv_group.setEmptyView(progressGroup);
        adapter.notifyDataSetChanged();
    }

    private void getData() {

        if(Network.isOnline(getActivity().getApplicationContext())) {

            API.centerService.getCenter(new Payload(), new Callback<CollectionSheet>() {
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
}
