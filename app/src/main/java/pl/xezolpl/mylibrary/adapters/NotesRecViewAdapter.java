package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class NotesRecViewAdapter extends RecyclerView.Adapter<NotesRecViewAdapter.ViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private Context context;

    public NotesRecViewAdapter(Context context) {
        this.context = context;
    }

    public void setNotesList(List<Note> notes) {
        this.notes = notes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_note, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    //Inner classes
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView note_text;
        private ImageView note_marker_imgView;
        private RecyclerView note_recView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            note_text = (TextView) itemView.findViewById(R.id.note_text);
            note_marker_imgView = (ImageView) itemView.findViewById(R.id.note_marker_imgView);
            note_recView = (RecyclerView) itemView.findViewById(R.id.note_recView);
        }

        public void setData(Note note) {
            note_text.setText(note.getNote());

            try {
                NoteViewModel viewModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
                viewModel.getNotesByParent(note.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        NotesRecViewAdapter adapter = new NotesRecViewAdapter(context);
                        adapter.setNotesList(notes);
                        note_recView.setAdapter(adapter);
                        note_recView.setLayoutManager(new GridLayoutManager(context,1));
                    }
                });
            } catch (NullPointerException exc) {
                exc.printStackTrace();
            }
        }
    }
}
