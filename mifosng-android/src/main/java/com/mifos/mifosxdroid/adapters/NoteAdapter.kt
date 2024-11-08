package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.core.objects.noncore.Note
import com.mifos.mifosxdroid.databinding.ItemNoteBinding
import javax.inject.Inject

/**
 * Created by rahul on 4/3/17.
 */
class NoteAdapter @Inject constructor() : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var notesList: List<Note>

    init {
        notesList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.binding.tvNote.text = note.noteContent
    }

    fun setNotes(notes: List<Note>) {
        this.notesList = notes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun getItem(position: Int): Note {
        return notesList[position]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)
}