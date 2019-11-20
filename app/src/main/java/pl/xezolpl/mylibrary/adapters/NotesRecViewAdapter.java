package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        private RelativeLayout note_relLay;

        private LinearLayout optionsLay;
        private Button addBtn, editBtn, deleteBtn;

        private NotesRecViewAdapter adapter;
        private boolean isRecViewVisible = false;
        private boolean isOptionsLayVisible = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            initWidgets();
            setOnClickListeners();

            note_recView.setVisibility(View.GONE);
            optionsLay.setVisibility(View.GONE);
        }

        private void setOnClickListeners() {

            note_relLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isRecViewVisible) {
                        note_recView.setVisibility(View.GONE);
                        isRecViewVisible = false;
                    } else {
                        note_recView.setVisibility(View.VISIBLE);
                        isRecViewVisible = true;
                    }
                }
            });

            note_relLay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (isOptionsLayVisible) {
                        optionsLay.setVisibility(View.GONE);
                        isOptionsLayVisible = false;
                    } else {
                        optionsLay.setVisibility(View.VISIBLE);
                        isOptionsLayVisible = true;
                    }
                    return true;
                }
            });

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void initWidgets() {

            note_text = (TextView) itemView.findViewById(R.id.note_text);
            note_marker_imgView = (ImageView) itemView.findViewById(R.id.note_marker_imgView);
            note_recView = (RecyclerView) itemView.findViewById(R.id.note_recView);
            note_relLay = (RelativeLayout) itemView.findViewById(R.id.note_relLay);

            optionsLay = (LinearLayout) itemView.findViewById(R.id.optionsLay);
            addBtn = (Button) itemView.findViewById(R.id.addBtn);
            editBtn = (Button) itemView.findViewById(R.id.editBtn);
            deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }

        public void setData(Note note) {
            note_text.setText(note.getNote());

            try {
                NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
                noteModel.getNotesByParent(note.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        adapter = new NotesRecViewAdapter(context);
                        adapter.setNotesList(notes);
                        note_recView.setAdapter(adapter);
                        note_recView.setLayoutManager(new GridLayoutManager(context, 1));
                    }
                });
            } catch (NullPointerException exc) {
                exc.printStackTrace();
            }
        }
    }
}
