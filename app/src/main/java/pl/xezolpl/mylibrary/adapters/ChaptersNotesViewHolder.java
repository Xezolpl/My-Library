package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddChapterActivity;
import pl.xezolpl.mylibrary.activities.AddNoteActivity;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class ChaptersNotesViewHolder extends RecyclerView.ViewHolder {
    public static final int FROM_CHAPTER = 1;
    public static final int FROM_NOTE = 2;

    public static final int EDIT_REQUEST = 3;
    public static final int DELETE_REQUEST = 4;

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
    private Chapter parentChapter = null;
    private Note parentNote = null;

    public ChaptersNotesViewHolder(@NonNull View itemView, Context context, int parent) {
        super(itemView);
        this.context = context;

        initWidgets();
        setOnClickListeners();

        setRecViewVisible(false);
        setOptionsLayVisible(false);
    }

    private void setOnClickListeners() {

        wholeRelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecViewVisible) {
                    setRecViewVisible(false);
                } else {
                    setRecViewVisible(true);
                }
            }
        });

        wholeRelLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isOptionsLayVisible) {
                    setOptionsLayVisible(false);
                } else {
                    setOptionsLayVisible(true);
                }
                return false;
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(context, AddNoteActivity.class);
                    if (parent == FROM_CHAPTER) intent.putExtra("chapter", parentChapter);
                    else intent.putExtra("note", parentNote);
                    context.startActivity(intent);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    Toast.makeText(context, "Something went wrong! Try again or restart application.", Toast.LENGTH_SHORT);
                }

                setOptionsLayVisible(false);
                setRecViewVisible(true);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(parent == FROM_CHAPTER){
                    intent = new Intent(context, AddChapterActivity.class);
                    intent.putExtra("chapter", parentChapter);
                }else {
                    intent = new Intent(context, AddNoteActivity.class);
                    intent.putExtra("note", parentNote);
                }
                intent.putExtra("request", EDIT_REQUEST);
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parent  == FROM_CHAPTER){
                    ChapterViewModel model = ViewModelProviders.of((FragmentActivity)context).get(ChapterViewModel.class);
                    model.delete(parentChapter);
                }else {
                    NoteViewModel model  = ViewModelProviders.of((FragmentActivity)context).get(NoteViewModel.class);
                    model.delete(parentNote);
                    setOptionsLayVisible(false);
                    setRecViewVisible(false);
                }
            }
        });
    }

    private void initWidgets() {

        textView = (TextView) itemView.findViewById(R.id.textView);
        recView = (RecyclerView) itemView.findViewById(R.id.recView);
        wholeRelLay = (RelativeLayout) itemView.findViewById(R.id.wholeRelLay);
        marker_imgView = (ImageView) itemView.findViewById(R.id.marker_imgView);

        optionsLay = (LinearLayout) itemView.findViewById(R.id.optionsLay);
        addBtn = (Button) itemView.findViewById(R.id.addBtn);
        editBtn = (Button) itemView.findViewById(R.id.editBtn);
        deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);

    }

    public void setData(Chapter chapter) {

        parentChapter = chapter;
        parent = FROM_CHAPTER;
        textView.setText(chapter.getName());
        try {
            NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
            noteModel.getNotesByParent(chapter.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
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

    public void setData(Note note) {

        parentNote = note;
        parent = FROM_NOTE;
        textView.setText(note.getNote());
        try {
            NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
            noteModel.getNotesByParent(note.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
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

    public void setOptionsLayVisible(boolean b){
        if(b){
            optionsLay.setVisibility(View.VISIBLE);
            isOptionsLayVisible = true;
        }
        else{
            optionsLay.setVisibility(View.GONE);
            isOptionsLayVisible = false;
        }
    }

    public void setRecViewVisible(boolean b){
        if(b){
            recView.setVisibility(View.VISIBLE);
            isRecViewVisible = true;
        }
        else{
            recView.setVisibility(View.GONE);
            isRecViewVisible = false;
        }
    }
}

