package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.View;
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

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class ChaptersNotesViewHolder extends RecyclerView.ViewHolder {
    public static final int FROM_CHAPTER=1;
    public static final int FROM_NOTE=2;

    private TextView textView;
    private RecyclerView recView;
    private ImageView marker_imgView;
    private RelativeLayout wholeRelLay;

    private LinearLayout optionsLay;
    private Button addBtn, editBtn, deleteBtn;

    private NotesRecViewAdapter adapter;
    private boolean isRecViewVisible = false;
    private boolean isOptionsLayVisible = false;
    private Context context;

    private int parent;

    public ChaptersNotesViewHolder(@NonNull View itemView, Context context, int parent) {
        super(itemView);
        this.context = context;
        this.parent = parent;

        initWidgets();
        setOnClickListeners();

        recView.setVisibility(View.GONE);
        optionsLay.setVisibility(View.GONE);
    }

    private void setOnClickListeners() {

        wholeRelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecViewVisible) {
                    recView.setVisibility(View.GONE);
                    isRecViewVisible = false;
                } else {
                    recView.setVisibility(View.VISIBLE);
                    isRecViewVisible = true;
                }
            }
        });

        wholeRelLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isOptionsLayVisible) {
                    optionsLay.setVisibility(View.GONE);
                    isOptionsLayVisible = false;
                } else {
                    optionsLay.setVisibility(View.VISIBLE);
                    isOptionsLayVisible = true;
                }
                return false;
            }
        });
    }

    private void initWidgets() {

        textView = (TextView) itemView.findViewById(R.id.textView);
        recView = (RecyclerView) itemView.findViewById(R.id.recView);
        wholeRelLay = (RelativeLayout) itemView.findViewById(R.id.wholeRelLay);

        if(parent==2) marker_imgView = (ImageView) itemView.findViewById(R.id.marker_imgView);

        optionsLay = (LinearLayout) itemView.findViewById(R.id.optionsLay);
        addBtn = (Button) itemView.findViewById(R.id.addBtn);
        editBtn = (Button) itemView.findViewById(R.id.editBtn);
        deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
    }

    public void setData(String id, String text) {
        textView.setText(text);
        try {
            NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity)context).get(NoteViewModel.class);
            noteModel.getNotesByParent(id).observe((FragmentActivity) context, new Observer<List<Note>>() {
                @Override
                public void onChanged(List<Note> notes) {
                    adapter = new NotesRecViewAdapter(context);
                    adapter.setNotesList(notes);
                    recView.setAdapter(adapter);
                    recView.setLayoutManager(new GridLayoutManager(context, 1));
                }
            });
        } catch (NullPointerException exc) {
            exc.printStackTrace();
        }
    }
}

