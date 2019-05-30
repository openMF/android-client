/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.Document;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 02/07/14.
 */
public class DocumentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Document> documents = new ArrayList<Document>();

    @Inject
    public DocumentListAdapter() {
        documents = new ArrayList<>();
    }

    public Document getItem(int i) {
        return documents.get(i);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_document_list, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {

            ((ViewHolder) holder).tv_doc_name.setText(documents.get(position).getName());
            ((ViewHolder) holder).tv_doc_description
                    .setText(documents.get(position).getDescription() == null ?
                            "-" : documents.get(position).getDescription());

            MaterialIcons cloudIcon = MaterialIcons.md_cloud_download;
//        Iconify.IconValue cloudIcon = Iconify.IconValue.fa_download;

            //TODO Implement Local Storage Check to show File Download Info
            //Iconify.IconValue storageIcon = Iconify.IconValue.fa_hdd_o;

            ((ViewHolder) holder).tv_doc_location_icon.setText("{" + cloudIcon.key() + "}");

//        Iconify.addIcons(reusableDocumentViewHolder.tv_doc_location_icon);
        }
    }

    public void setDocuments(List<Document> documentList) {
        documents = documentList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_doc_name)
        TextView tv_doc_name;

        @BindView(R.id.tv_doc_descrption)
        TextView tv_doc_description;

        @BindView(R.id.tv_doc_location_icon)
        IconTextView tv_doc_location_icon;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
