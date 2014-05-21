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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mifos.mifosxdroid.LoanActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientListAdapter;
import com.mifos.objects.db.Client;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;


public class ClientFragment extends Fragment implements AdapterView.OnItemClickListener
{
    @InjectView(R.id.lv_clients)
     ListView lv_clients;
    private ClientListAdapter adapter = null;
    private long groupId;
    private List<Client> clientsInTheGroup = new ArrayList<Client>();
    final private String tag = getClass().getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_client, null);
        ButterKnife.inject(this, view);
        getGroupId();
        setAdapter();
        return view;
    }

    private long getGroupId()
    {
        groupId =  getArguments().getLong("group_id",0);
        return groupId;
    }

    private void setAdapter()
    {
        getClients();
        if (adapter == null)
            adapter = new ClientListAdapter(getActivity(), clientsInTheGroup);
        lv_clients.setAdapter(adapter);
        lv_clients.setOnItemClickListener(this);
    }
    private List<Client> getClients()
    {
        clientsInTheGroup.clear();
        List<Client> clients  = Select.from(Client.class).where(Condition.prop("mifos_group").eq(groupId)).list();

        Log.i(tag,"Clients in ClientFragment from DB:"+clients.toString());
        clientsInTheGroup.addAll(clients);
        return clientsInTheGroup;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        final int clientId =  clientsInTheGroup.get(i).getClientId();
        Log.i(tag,"onItemClick:-clientId:"+clientId);
        Intent intent = new Intent(getActivity(),LoanActivity.class);
        intent.putExtra("clientId",clientId);
        startActivity(intent);
    }
}