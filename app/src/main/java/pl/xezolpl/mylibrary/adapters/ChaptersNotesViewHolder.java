package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
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
    private Button moreBtn;

    private NotesRecViewAdapter adapter;
    private QuotesRecViewAdapter quotesAdapter;

    private boolean isRecViewVisible = true;
    private int parent;

    private Context context;
    private FragmentActivity activity;

    private Chapter parentChapter = null;
    private Note parentNote = null;

    ChaptersNotesViewHolder(@NonNull View itemView, Context context, int parent) {
        super(itemView);
        this.context = context;
        this.parent = parent;
        activity = (FragmentActivity) context;

        initWidgets();
        setOnClickListeners();

        if (parent == FROM_CHAPTER) {
            quotesAdapter = new QuotesRecViewAdapter(context);
            quotesRecView.setLayoutManager(new GridLayoutManager(context, 1));
        }

    }

    private void initWidgets() {

        textView = itemView.findViewById(R.id.textView);
        recView = itemView.findViewById(R.id.recView);
        wholeRelLay = itemView.findViewById(R.id.wholeRelLay);
        if (parent == FROM_NOTE)
            marker_imgView = itemView.findViewById(R.id.marker_imgView);
        else {
            quotesRecView = itemView.findViewById(R.id.quotes_recView);
            setRecViewVisible(false);
        }
        moreBtn = itemView.findViewById(R.id.moreBtn);

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
                //TODO:EXPAND EVERY CHILD
                return false;
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.chapter_note_popup_menu);
                if (parent == FROM_NOTE) {
                    popupMenu.getMenu().getItem(2).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addMenuBtn: {

                                Intent intent = new Intent(context, AddNoteActivity.class);
                                if (parent == FROM_CHAPTER) {
                                    intent.putExtra("chapter", parentChapter);
                                    intent.putExtra("parent", FROM_CHAPTER);
                                } else {
                                    intent.putExtra("note", parentNote);
                                    intent.putExtra("parent", FROM_NOTE);
                                }
                                setRecViewVisible(true);
                                context.startActivity(intent);
                                break;
                            }
                            case R.id.editMenuBtn: {

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
                                break;
                            }
                            case R.id.insertQuoteMenuBtn: {

                                Intent intent = new Intent(context, InsertQuoteActivity.class);
                                intent.putExtra("chapter", parentChapter);

                                context.startActivity(intent);
                                break;
                            }
                            case R.id.deleteMenuBtn: {

                                if (parent == FROM_CHAPTER) {
                                    ChapterViewModel model = ViewModelProviders.of(activity).get(ChapterViewModel.class);
                                    model.delete(parentChapter);
                                } else {
                                    NoteViewModel model = ViewModelProviders.of(activity).get(NoteViewModel.class);
                                    model.delete(parentNote);
                                    setRecViewVisible(false);
                                }
                                break;
                            }
                            default:
                                return false;
                        }
                        return true;
                    }
                });

                MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), view);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            }
        });

    }

    void setData(Chapter chapter) {

        parentChapter = chapter;
        textView.setText(chapter.getName());
        NoteViewModel noteModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        noteModel.getNotesByParent(chapter.getId()).observe(activity, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        adapter = new NotesRecViewAdapter(context);
                        adapter.setNotesList(notes);
                        recView.setAdapter(adapter);
                        recView.setLayoutManager(new GridLayoutManager(context, 1));

                        QuoteViewModel quoteViewModel = ViewModelProviders.of(activity).get(QuoteViewModel.class);
                        quoteViewModel.getQuotesByChapter(parentChapter.getId()).observe(activity, new Observer<List<Quote>>() {
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

    void setData(Note note, int position) {

        parentNote = note;
        textView.setText(note.getNote());
        try {
            Drawable drawable;
            int markerType = note.getMarkerType();

            if (markerType == Markers.NUMBER_MARKER || markerType == Markers.LETTER_MARKER) {
                drawable = Markers.getLetterMarker(markerType, position, note.getColor(), TextDrawable.BIG_TEXT_SIZE);
            } else {
                drawable = Markers.getSimpleMarker(context, note.getMarkerType(), note.getColor());
            }

            marker_imgView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NoteViewModel noteModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        noteModel.getNotesByParent(note.getId()).observe(activity, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter = new NotesRecViewAdapter(context);
                adapter.setNotesList(notes);
                recView.setAdapter(adapter);
                recView.setLayoutManager(new GridLayoutManager(context, 1));
            }
        });
    }

    private void setRecViewVisible(boolean b) {
        if (b) {
            recView.setVisibility(View.VISIBLE);
            isRecViewVisible = true;
        } else {
            recView.setVisibility(View.GONE);
            isRecViewVisible = false;
        }
    }
}

