package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joanzapata.iconify.fonts.MaterialIcons
import com.joanzapata.iconify.widget.IconTextView
import com.mifos.mifosxdroid.R
import com.mifos.objects.noncore.Document
import javax.inject.Inject


class DocumentListAdapter(
    val onDocumentClick: (Document) -> Unit
) : RecyclerView.Adapter<DocumentListAdapter.ViewHolder>() {
    var documents: List<Document> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun getItem(i: Int) = documents[i]


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_document_list, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            if(viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                onDocumentClick(documents[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_doc_name.text = documents[position].name
        holder.tv_doc_description.text =
            if (documents[position].description == null) "-"
            else documents[position].description

        val cloudIcon = MaterialIcons.md_cloud_download

        //TODO Implement Local Storage Check to show File Download Info
        //Iconify.IconValue storageIcon = Iconify.IconValue.fa_hdd_o;
        holder.tv_doc_location_icon.text = "{" + cloudIcon.key() + "}"
    }

    override fun getItemId(i: Int) = 0L


    override fun getItemCount() = documents.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tv_doc_name: TextView = v.findViewById(R.id.tv_doc_name)
        val tv_doc_description: TextView = v.findViewById(R.id.tv_doc_descrption)
        val tv_doc_location_icon: IconTextView = v.findViewById(R.id.tv_doc_location_icon)
    }
}
