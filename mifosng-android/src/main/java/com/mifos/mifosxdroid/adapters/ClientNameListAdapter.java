/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.SelectableAdapter;
import com.mifos.objects.client.Client;
import com.mifos.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    private List<Client> pageItems;
    private Context mContext;

    @Inject
    public ClientNameListAdapter() {
        this.pageItems  = new ArrayList<>();
    }

    public Client getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_client_name, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {

            Client client = getItem(position);
            String clientName;
            if (client.getFullname() == null) {
                clientName = client.getFirstname() + " " + client.getLastname();
            } else {
                clientName = client.getFullname();
            }
            ((ViewHolder) holder).tv_clientName.setText(clientName);
            ((ViewHolder) holder).tv_clientAccountNumber.setText(client.getAccountNo());

            // lazy the  load profile picture
            if (client.isImagePresent()) {
                // make the image url
                ImageLoaderUtils.loadImage(mContext, client.getId(),
                        ((ViewHolder) holder).iv_userPicture);
            } else {
                ((ViewHolder) holder).iv_userPicture.setImageResource(R.drawable.ic_dp_placeholder);
            }

            //Changing the Color of Selected Clients
            ((ViewHolder) holder).view_selectedOverlay
                    .setBackgroundColor(isSelected(position) ? ContextCompat.getColor(mContext,
                            R.color.gray_light) : Color.WHITE);

            ((ViewHolder) holder).iv_sync_status
                    .setVisibility(client.isSync() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setClients(List<Client> clients) {
        pageItems = clients;
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_clientName)
        TextView tv_clientName;

        @BindView(R.id.tv_clientAccountNumber)
        TextView tv_clientAccountNumber;

        @BindView(R.id.iv_user_picture)
        ImageView iv_userPicture;

        @BindView(R.id.linearLayout)
        LinearLayout view_selectedOverlay;

        @BindView(R.id.iv_sync_status)
        ImageView iv_sync_status;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
