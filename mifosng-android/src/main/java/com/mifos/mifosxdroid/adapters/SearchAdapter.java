/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.SearchedEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author fomenkoo
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private List<SearchedEntity> searchedResults;

    @Inject
    public SearchAdapter() {
        mDrawableBuilder = TextDrawable.builder().round();
        searchedResults = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_client, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchAdapter.ViewHolder) {
            SearchedEntity searchedEntity = searchedResults.get(position);
            ((ViewHolder) holder).tv_name.setText(searchedEntity.getDescription());

            TextDrawable drawable;
            if (searchedEntity.getEntityName() != null &&
                    searchedEntity.getEntityName().trim().length() > 0) {
                drawable = mDrawableBuilder.build(String.valueOf(
                        searchedEntity.getEntityType().charAt(0)),
                        mColorGenerator.getColor(searchedEntity.getEntityType()));
                ((ViewHolder) holder).iv_icon.setImageDrawable(drawable);
            }
        }
    }

    @Override
    public int getItemCount() {
        return searchedResults.size();
    }

    public void setSearchResults(List<SearchedEntity> searchedResults) {
        this.searchedResults = searchedResults;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_icon)
        ImageView iv_icon;

        @BindView(R.id.tv_name)
        TextView tv_name;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
