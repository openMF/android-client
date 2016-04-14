package com.mifos.mifosxdroid.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mifos.api.model.ClientAddress;
import com.mifos.mifosxdroid.R;

import java.util.List;

/**
 * Created by Tarun on 02/04/2016.
 */
public class ClientAddressAdapter extends ArrayAdapter <ClientAddress> {

    public ClientAddressAdapter(Activity context, List<ClientAddress> clientAddressList) {
          super(context, 0, clientAddressList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.client_address_list, parent, false);
        }
        TextView cityTextView = (TextView) convertView.findViewById(R.id.tv_address_city);
        TextView stateTextview = (TextView) convertView.findViewById(R.id.tv_address_state);
        TextView latTextView = (TextView) convertView.findViewById(R.id.tv_address_lat);
        TextView longTextView = (TextView) convertView.findViewById(R.id.tv_address_long);
        TextView placeholderTextView = (TextView) convertView.findViewById(R.id.tv_no_location_placeholder);

        stateTextview.setText(getItem(position).getState());
        cityTextView.setText(getItem(position).getCity());

        double lat = getItem(position).getLat();
        double longi = getItem(position).getLong();
        if (lat!=0.0 && longi !=0.0){
            latTextView.setText(String.format("%f", lat));
            longTextView.setText(String.format("%f", longi));
        }
        else {
            longTextView.setVisibility(View.GONE);
            latTextView.setVisibility(View.GONE);
            placeholderTextView.setVisibility(View.VISIBLE);
            placeholderTextView.setText(R.string.latlng_not_available);
        }

        return convertView;
    }
}
