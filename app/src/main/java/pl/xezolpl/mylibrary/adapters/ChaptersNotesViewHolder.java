package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.io.IOException;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddChapterActivity;
import pl.xezolpl.mylibrary.activities.AddNoteActivity;
import pl.xezolpl.mylibrary.activities.InsertQuoteActivity;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.utilities.TextDrawable;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class ChaptersNotesViewHolder extends RecyclerView.ViewHolder {
    public static final int FROM_CHAPTER = 1;
    public static final int FROM_NOTE = 2;
    public static final int EDITION = 3;

    private TextView textView;
    private RecyclerView recView, quotesRecView;
    private ImageView marker_imgView;
    private RelativeLayout wholeRelLay;

    private LinearLayout optionsLay;
    private Button addBtn, editBtn, deleteBtn, insertQuoteBtn;

    private NotesRecViewAdapter adapter;
    private boolean isRecViewVisible = false;
    private boolean isOptionsLayVisible = false;
    private Context context;

    private int parent;
    private Chapter parentChapter = null;
    private Note parentNote = null;
    private QuotesRecViewAdapter quotesAdapter;

    public ChaptersNotesViewHolder(@NonNull View itemView, Context context, int parent) {
        super(itemView);
        this.context = context;
        this.parent = parent;

        initWidgets();
        setOnClickListeners();

        setRecViewVisible(false);
        setOptionsLayVisible(false);

        if (parent == FROM_CHAPTER) {
            quotesAdapter = new QuotesRecViewAdapter(context);
            quotesRecView.setLayoutManager(new GridLayoutManager(context, 1));
        }

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
                    if (parent == FROM_CHAPTER) {
                        intent.putExtra("chapter", parentChapter);
                        intent.putExtra("parent", FROM_CHAPTER);
                    } else {
                        intent.putExtra("note", parentNote);
                        intent.putExtra("parent", FROM_NOTE);
                    }
                    context.startActivity(intent);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    Toast.makeText(context, "Something went wrong! Try again or restart application.", Toast.LENGTH_SHORT).show();
                }

                setOptionsLayVisible(false);
                setRecViewVisible(true);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (parent == FROM_CHAPTER) {
                    intent = new Intent(context, AddChapterActivity.class);
                    intent.putExtra("chapter", parentChapter);
                } else {
                    intent = new Intent(context, AddNoteActivity.class);
                    intent.putExtra("note", parentNote);
                    intent.putExtra("parent", EDITION);
                }
                context.startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent == FROM_CHAPTER) {
                    ChapterViewModel model = ViewModelProviders.of((FragmentActivity) context).get(ChapterViewModel.class);
                    model.delete(parentChapter);
                } else {
                    NoteViewModel model = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
                    model.delete(parentNote);
                    setOptionsLayVisible(false);
                    setRecViewVisible(false);
                }
            }
        });

        if (parent == FROM_CHAPTER) {
            insertQuoteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, InsertQuoteActivity.class);
                    intent.putExtra("chapter", parentChapter);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void initWidgets() {

        textView = itemView.findViewById(R.id.textView);
        recView = itemView.findViewById(R.id.recView);
        wholeRelLay = itemView.findViewById(R.id.wholeRelLay);
        if (parent == FROM_NOTE)
            marker_imgView = itemView.findViewById(R.id.marker_imgView);
        else {
            insertQuoteBtn = itemView.findViewById(R.id.insertQuoteBtn);
            quotesRecView = itemView.findViewById(R.id.quotes_recView);
        }

        optionsLay = itemView.findViewById(R.id.optionsLay);
        addBtn = itemView.findViewById(R.id.addBtn);
        editBtn = itemView.findViewById(R.id.editBtn);
        deleteBtn = itemView.findViewById(R.id.deleteBtn);

    }

    public void setData(Chapter chapter) {

        parentChapter = chapter;
        textView.setText(chapter.getName());
        NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
        noteModel.getNotesByParent(chapter.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        adapter = new NotesRecViewAdapter(context);
                        adapter.setNotesList(notes);
                        recView.setAdapter(adapter);
                        recView.setLayoutManager(new GridLayoutManager(context, 1));

                        QuoteViewModel quoteViewModel = ViewModelProviders.of((FragmentActivity) context).get(QuoteViewModel.class);
                        quoteViewModel.getQuotesByChapter(parentChapter.getId()).observe((FragmentActivity) context, new Observer<List<Quote>>() {
                            @Override
                            public void onChanged(List<Quote> quotes) {
                                quotesAdapter.setQuotes(quotes);
                                quotesRecView.setAdapter(quotesAdapter);
                            }
                        });
                    }
                }
        );
    }

    public void setData(Note note, int position) {

        parentNote = note;
        textView.setText(note.getNote());
        try {
            Drawable drawable;
            int markerType = note.getMarkerType();

            if (markerType == Markers.NUMBER_MARKER || markerType == Markers.LETTER_MARKER){
                drawable = Markers.getLetterMarker(markerType, position, note.getColor(), TextDrawable.MEDIUM_TEXT_SIZE);
            }else{
                drawable = Markers.getSimpleMarker(context, note.getMarkerType(),note.getColor());
            }

                marker_imgView.setImageDrawable(drawable);
            //TODO: GET UPPER  NOTE'S markerPosition and set it +1 to it as letter/number
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    public void setOptionsLayVisible(boolean b) {
        if (b) {
            optionsLay.setVisibility(View.VISIBLE);
            isOptionsLayVisible = true;
        } else {
            optionsLay.setVisibility(View.GONE);
            isOptionsLayVisible = false;
        }
    }

    public void setRecViewVisible(boolean b) {
        if (b) {
            recView.setVisibility(View.VISIBLE);
            isRecViewVisible = true;
        } else {
            recView.setVisibility(View.GONE);
            isRecViewVisible = false;
        }
    }
}

