package com.mifos.mifosxdroid.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.TextDrawable.IBuilder
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.mifos.mifosxdroid.R
import com.mifos.objects.SearchedEntity
import javax.inject.Inject


class SearchAdapter(
    val onSearchItemClick: (SearchedEntity) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    private val mColorGenerator = ColorGenerator.MATERIAL
    private var mDrawableBuilder: IBuilder = TextDrawable.builder().round()
    private var searchedResults: List<SearchedEntity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_client, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
                onSearchItemClick(searchedResults[viewHolder.bindingAdapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchedEntity = searchedResults[position]
        holder.tvName.text = searchedEntity.description
        if (!searchedEntity.entityName.isNullOrBlank()) {
            val drawable = mDrawableBuilder.build(
                searchedEntity.entityType[0].toString(),
                mColorGenerator.getColor(searchedEntity.entityType)
            )
            holder.ivIcon.setImageDrawable(drawable)
        }
    }

    override fun getItemCount() = searchedResults.size


    fun setSearchResults(searchedResults: List<SearchedEntity>) {
        this.searchedResults = searchedResults
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivIcon: ImageView = v.findViewById(R.id.tv_icon)
        val tvName: TextView = v.findViewById(R.id.tv_name)
    }
}
