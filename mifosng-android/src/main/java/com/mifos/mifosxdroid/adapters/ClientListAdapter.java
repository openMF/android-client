package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.Client;

import java.util.List;


public class ClientListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Client> listClient;

    public ClientListAdapter(Context context, List<Client> listClient){

        layoutInflater = LayoutInflater.from(context);
        this.listClient = listClient;
    }

    @Override
    public int getCount() {
        return this.listClient.size();
    }

    @Override
    public Client getItem(int i) {
        return this.listClient.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if(view==null){
            view = layoutInflater.inflate(R.layout.row_client_list_item,viewGroup,false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Client client = listClient.get(i);
        viewHolder.tv_client_id = (TextView) view.findViewById(R.id.tv_clientId);
        viewHolder.tv_client_name = (TextView) view.findViewById(R.id.tv_clientName);
        viewHolder.tv_attendance_type = (TextView) view.findViewById(R.id.tv_attendance_type);

        viewHolder.tv_client_id.setText(String.valueOf(client.getClientId()));
        viewHolder.tv_client_name.setText(client.getClientName());
//        viewHolder.tv_attendance_type.setText(client.getAttendanceType().getValue());

        return view;
    }

    public static class ViewHolder{

        TextView tv_client_name;
        TextView tv_client_id;
        TextView tv_attendance_type;
    }
}
