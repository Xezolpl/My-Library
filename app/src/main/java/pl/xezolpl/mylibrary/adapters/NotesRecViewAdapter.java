package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Note;

public class NotesRecViewAdapter extends RecyclerView.Adapter<ChaptersNotesViewHolder> {

    private List<Note> notes = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;

    public NotesRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setNotesList(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChaptersNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.listitem_note, parent, false);
        return new ChaptersNotesViewHolder(v, context, ChaptersNotesViewHolder.FROM_NOTE);
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersNotesViewHolder holder, int position) {
        holder.setData(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}