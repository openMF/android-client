/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mifos.api.MifosInterceptor;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.SelectableAdapter;
import com.mifos.objects.client.Client;
import com.mifos.utils.PrefManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {

    LayoutInflater layoutInflater;
    private List<Client> pageItems;
    private Context mContext;

    public ClientNameListAdapter(Context context, List<Client> pageItems) {

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.mContext = context;
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
            String clientName = client.getFirstname() + " " + client.getLastname();
            ((ViewHolder) holder).tv_clientName.setText(clientName);
            ((ViewHolder) holder).tv_clientAccountNumber.setText(client.getAccountNo());

            // lazy the  load profile picture
            if (client.isImagePresent()) {

                // make the image url
                String url = PrefManager.getInstanceUrl()
                        + "clients/"
                        + client.getId()
                        + "/images?maxHeight=120&maxWidth=120";
                GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                        .addHeader(MifosInterceptor.HEADER_TENANT, PrefManager.getTenant())
                        .addHeader(MifosInterceptor.HEADER_AUTH, PrefManager.getToken())
                        .addHeader("Accept", "application/octet-stream")
                        .build());

                // download the image from the url
                Glide.with(mContext)
                        .load(glideUrl)
                        .asBitmap()
                        .placeholder(R.drawable.ic_dp_placeholder)
                        .error(R.drawable.ic_dp_placeholder)
                        .into(new BitmapImageViewTarget(((ViewHolder) holder).iv_userPicture) {
                            @Override
                            protected void setResource(Bitmap result) {
                                // check a valid bitmap is downloaded
                                if (result == null || result.getWidth() == 0)
                                    return;

                                // set to image view
                                ((ViewHolder) holder).iv_userPicture.setImageBitmap(result);
                            }
                        });
            } else {
                ((ViewHolder) holder).iv_userPicture.setImageResource(R.drawable.ic_dp_placeholder);
            }

            //Changing the Color of Selected Clients
            ((ViewHolder) holder).view_selectedOverlay
                    .setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_clientName)
        TextView tv_clientName;

        @BindView(R.id.tv_clientAccountNumber)
        TextView tv_clientAccountNumber;

        @BindView(R.id.iv_user_picture)
        ImageView iv_userPicture;

        @BindView(R.id.selected_overlay)
        View view_selectedOverlay;

        @BindView(R.id.iv_sync_status)
        ImageView iv_sync_status;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
