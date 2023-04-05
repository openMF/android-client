package com.mifos.mifosxdroid.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.databinding.ItemNoteBinding;
import com.mifos.objects.noncore.Note;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by rahul on 4/3/17.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    List<Note> notes;

    @Inject
    public NoteAdapter() {
        notes = new ArrayList<>();
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemNoteBinding binding = ItemNoteBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.tvNote.setText(note.getNoteContent());
    }


    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;

        public ViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            tvNote = binding.tvNote;
        }
    }
}
