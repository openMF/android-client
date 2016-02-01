/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.adapters.MifosBaseListAdapter;
import com.mifos.objects.client.Client;
import java.util.List;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class ClientChooseAdapter extends MifosBaseListAdapter<Client> {
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public ClientChooseAdapter(Context context, List<Client> list, int layoutId) {
        super(context, list, layoutId);
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Client item;

        if (convertView == null) {
            convertView = getLayout();
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        item = getItem(position);
        holder.tv_clientName.setText(item.getDisplayName());
        TextDrawable drawable = mDrawableBuilder.build(String.valueOf(item.getDisplayName().charAt(0)), mColorGenerator.getColor(item.getDisplayName()));
        holder.iv_clientImage.setImageDrawable(drawable);
        return convertView;
    }

    public static class ViewHolder {
        private ImageView iv_clientImage;
        private TextView tv_clientName;

        public ViewHolder(View view) {
            iv_clientImage = (ImageView) view.findViewById(R.id.icon);
            tv_clientName = (TextView) view.findViewById(R.id.name);
        }
    }
}
