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

import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.Identifier;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class IdentifierListAdapter extends RecyclerView.Adapter<IdentifierListAdapter.ViewHolder> {

    private List<Identifier> identifiers;
    private IdentifierOptionsListener identifierOptionsListener;

    @Inject
    public IdentifierListAdapter() {
        identifiers = new ArrayList<>();
    }

    @Override
    public IdentifierListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_identifier_list, parent, false);
        return new IdentifierListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final IdentifierListAdapter.ViewHolder holder, int position) {

        final Identifier identifier = identifiers.get(position);

        holder.tv_identifier_id.setText(String.valueOf(identifier.getDocumentKey()));
        holder.tv_identifier_description.setText(identifier.getDescription());
        holder.tv_identifier_type.setText(identifier.getDocumentType().getName());
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
        notifyDataSetChanged();
    }

    public void setIdentifierOptionsListener(IdentifierOptionsListener identifierOptionsListener) {
        this.identifierOptionsListener = identifierOptionsListener;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return identifiers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_identifier_id)
        TextView tv_identifier_id;

        @BindView(R.id.tv_identifier_type)
        TextView tv_identifier_type;

        @BindView(R.id.tv_identifier_description)
        TextView tv_identifier_description;

        @BindView(R.id.iv_identifier_options)
        ImageView iv_identifier_options;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            iv_identifier_options.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            identifierOptionsListener.onClickIdentifierOptions(getAdapterPosition(), v);
        }
    }

    public interface IdentifierOptionsListener {
        void onClickIdentifierOptions(int position, View view);
    }
}