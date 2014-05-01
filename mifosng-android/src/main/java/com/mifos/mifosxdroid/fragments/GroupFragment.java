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
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.ClientActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupListAdapter;
import com.mifos.objects.db.*;
import com.mifos.objects.db.AttendanceType;
import com.mifos.objects.db.Client;
import com.mifos.objects.db.Currency;
import com.mifos.objects.db.Loan;
import com.mifos.objects.db.MifosGroup;
import com.mifos.utils.services.data.*;
import com.mifos.utils.services.API;
import com.orm.query.Select;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment implements AdapterView.OnItemClickListener
{
    @InjectView(R.id.lv_group)
    ListView lv_group;
    GroupListAdapter adapter = null;
    private final List<MifosGroup> groupList = new ArrayList<MifosGroup>();
    String tag = getClass().getSimpleName();
    View view ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view =  inflater.inflate(R.layout.fragment_group,null);
        ButterKnife.inject(this,view);
        getData();
        return view;
    }

    private void setAdapter()
    {
        if(adapter==null)
            adapter = new GroupListAdapter(getActivity(),groupList);
        if(lv_group==null)
        {
            lv_group = (ListView) view.findViewById(R.id.lv_group);
        }
        lv_group.setAdapter(adapter);
        lv_group.setOnItemClickListener(this);

    }
    private void getData()
    {
        API.centerService.getCenter(new Payload(), new Callback<CollectionSheet>() {

            @Override
            public void success(CollectionSheet collectionSheet, Response arg1) {
                if(collectionSheet !=null)
                {
                    Log.i(tag, "CollectionSheet\n"+ collectionSheet.toString());
                    insertGroupData(collectionSheet);
                    setAdapter();
                }
                else
                    Log.i(tag, "null is null null null null null null");
            }

            @Override
            public void failure(RetrofitError arg0) {
                Toast.makeText(getActivity(), "There was some error fetching data.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private List<MifosGroup> insertGroupData(CollectionSheet collectionSheet)
    {

        for(MifosGroup group : collectionSheet.groups){

            group.save();

            List<Client> clients = group.getClients();

            for(Client client: clients){
                if(isNewClient(client.getClientId()))
                {
                    client.setMifosGroup(group);
                    client.save();

                    AttendanceType attendanceType = client.getAttendanceType();
                    attendanceType.setClient(client);
                    attendanceType.save();
                }

                List<Loan> loans = client.getLoans();
                for(Loan loan : loans){

                    loan.setClient(client);
                    loan.save();

                    Currency currency = loan.getCurrency();
                    currency.setLoan(loan);
                    currency.save();

                }
            }
        }

        groupList.addAll(collectionSheet.groups);
        return groupList;
    }

    private boolean isNewGroup(int groupId)
    {
        boolean isNewGroup = true;
        List<MifosGroup> groups = Select.from(MifosGroup.class).list();
        for(MifosGroup group:groups)
        {
            if(group.getGroupId()==groupId)
            {
                isNewGroup = false;
                break;
            }
        }
        return isNewGroup;

    }

    private boolean isNewClient(int clientId)
    {
        boolean isNewClient = true;
        List<Client> clients = Select.from(Client.class).list();
        for(Client client:clients)
        {
            if(client.getClientId()== clientId)
            {
                isNewClient = false;
                break;
            }
        }
        return isNewClient;

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(getActivity(), ClientActivity.class);
        Log.i(tag,"Group Id:"+groupList.get(i).getGroupId());
        intent.putExtra("group_id",groupList.get(i).getId());
        startActivity(intent);
    }
}
