package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.ClientPayload;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 08/07/16.
 */
public class SyncPayloadsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LayoutInflater layoutInflater;
    private List<ClientPayload> clientPayloads;

    public SyncPayloadsAdapter(Context context, List<ClientPayload> payloads) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.clientPayloads = payloads;
    }

    public ClientPayload getItem(int position) {
        return clientPayloads.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_sync_client, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {

            ClientPayload clientPayload = clientPayloads.get(position);

            ((ViewHolder) holder).tv_first_name.setText(clientPayload.getFirstname());
            ((ViewHolder) holder).tv_middle_name.setText(clientPayload.getMiddlename());
            ((ViewHolder) holder).tv_last_name.setText(clientPayload.getLastname());
            ((ViewHolder) holder).tv_mobile_no.setText(clientPayload.getMobileNo());
            ((ViewHolder) holder).tv_external_id.setText(clientPayload.getExternalId());
            ((ViewHolder) holder).tv_dob.setText(clientPayload.getDateOfBirth());
            ((ViewHolder) holder).tv_office_id.setText(String.valueOf(clientPayload.getOfficeId()));
            ((ViewHolder) holder).tv_activation_date.setText(clientPayload.getActivationDate());

            switch (clientPayload.getGenderId()) {
                case 22 :
                    ((ViewHolder) holder).tv_gender.setText("Male");
                    break;
                case 24 :
                    ((ViewHolder) holder).tv_gender.setText("Female");
                    break;
                case 91 :
                    ((ViewHolder) holder).tv_gender.setText("homosexual");
                    break;
                default:
                    ((ViewHolder) holder).tv_gender.setText("Male");
                    break;
            }

            if (clientPayload.isActive()) {
                ((ViewHolder) holder).tv_active_status.setText(String.valueOf(true));
            } else {
                ((ViewHolder) holder).tv_active_status.setText(String.valueOf(false));
            }

            if (clientPayloads.get(position).getErrorMessage() != null) {
                ((ViewHolder) holder).tv_error_message.setText(clientPayload.getErrorMessage());
                ((ViewHolder) holder).tv_error_message.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return clientPayloads.size();
    }


    public void setClientPayload(List<ClientPayload> clientPayload) {
        clientPayloads = clientPayload;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_db_first_name)
        TextView tv_first_name;

        @BindView(R.id.tv_db_middle_name)
        TextView tv_middle_name;

        @BindView(R.id.tv_db_last_name)
        TextView tv_last_name;

        @BindView(R.id.tv_db_mobile_no)
        TextView tv_mobile_no;

        @BindView(R.id.tv_db_externalId)
        TextView tv_external_id;

        @BindView(R.id.tv_db_gender)
        TextView tv_gender;

        @BindView(R.id.tv_db_dob)
        TextView tv_dob;

        @BindView(R.id.tv_db_office_id)
        TextView tv_office_id;

        @BindView(R.id.tv_db_activation_date)
        TextView tv_activation_date;

        @BindView(R.id.tv_db_active_status)
        TextView tv_active_status;

        @BindView(R.id.tv_error_message)
        TextView tv_error_message;


        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
