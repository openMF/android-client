/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mifos.api.ApiRequestInterceptor;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.utils.PrefManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 27/02/14.
 */
public class ClientNameListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    List<Client> pageItems;
    private Context mContext;

    public ClientNameListAdapter(Context context, List<Client> pageItems){

        layoutInflater = LayoutInflater.from(context);
        this.pageItems = pageItems;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return pageItems.size();
    }

    @Override
    public Client getItem(int position) {
        return pageItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final ReusableViewHolder reusableViewHolder;

        if(view==null)
        {
            view = layoutInflater.inflate(R.layout.row_client_name,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        }else
        {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        Client client = getItem(position);
        reusableViewHolder.tv_clientName.setText(client.getFirstname()+" " +client.getLastname());
        reusableViewHolder.tv_clientAccountNumber.setText(client.getAccountNo().toString());

        // lazy the  load profile picture
        if (client.isImagePresent()) {

            // make the image url
            String url = PrefManager.getInstanceUrl()
                    + "/"
                    + "clients/"
                    + client.getId()
                    + "/images?maxHeight=120&maxWidth=120";
            GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader(ApiRequestInterceptor.HEADER_TENANT, PrefManager.getTenant())
                    .addHeader(ApiRequestInterceptor.HEADER_AUTH, PrefManager.getToken())
                    .addHeader("Accept", "application/octet-stream")
                    .build());

            // download the image from the url
            Glide.with(mContext)
                    .load(glideUrl)
                    .asBitmap()
                    .placeholder(R.drawable.ic_dp_placeholder)
                    .error(R.drawable.ic_dp_placeholder)
                    .into(new BitmapImageViewTarget(reusableViewHolder.iv_userPicture) {
                        @Override
                        protected void setResource(Bitmap result) {
                            // check a valid bitmap is downloaded
                            if (result == null || result.getWidth() == 0)
                                return;

                            // set to image view
                            reusableViewHolder.iv_userPicture.setImageBitmap(result);
                        }
                    });
        } else
        {
            reusableViewHolder.iv_userPicture.setImageResource(R.drawable.ic_dp_placeholder);
        }

        return view;
    }

     static class ReusableViewHolder{

         @InjectView(R.id.tv_clientName) TextView tv_clientName;
         @InjectView(R.id.tv_clientAccountNumber) TextView tv_clientAccountNumber;
         @InjectView(R.id.iv_user_picture) ImageView iv_userPicture;

         public ReusableViewHolder(View view) {
             ButterKnife.inject(this, view);
         }

    }
}
