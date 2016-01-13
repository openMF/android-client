package com.mifos.mifosxdroid.adapters;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.adapters.BaseListAdapter;
import com.mifos.objects.client.Client;
import java.util.List;

/**
 * Created by Nasim Banu on 19,January,2016.
 */
public class ClientChooseAdapter extends BaseListAdapter<Client> {
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
        holder.clientnameView.setText(item.getDisplayName());
        TextDrawable drawable = mDrawableBuilder.build(String.valueOf(item.getDisplayName().charAt(0)), mColorGenerator.getColor(item.getDisplayName()));
        holder.clientimageView.setImageDrawable(drawable);
        return convertView;
    }

    class ViewHolder {
        private ImageView clientimageView;
        private TextView clientnameView;

        public ViewHolder(View view) {
            clientimageView = (ImageView) view.findViewById(R.id.icon);
            clientnameView = (TextView) view.findViewById(R.id.name);
        }
    }
}

