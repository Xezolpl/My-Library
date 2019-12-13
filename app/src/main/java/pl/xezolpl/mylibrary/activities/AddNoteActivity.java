package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.ChaptersNotesViewHolder;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.utilities.TextDrawable;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class AddNoteActivity extends AppCompatActivity {

    private EditText add_note_name;
    private ImageView add_note_imgView;
    private Button ok_btn, cancel_btn;

    private NoteViewModel viewModel;

    private int currentMarkerType = Markers.DOT_MARKER;

    private String parentId;
    private String id;

    private boolean inEdition = false;
    private boolean markerTypeLocked = false;

    private Note thisNote;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initWidgets();
        setOnClickListeners();
        viewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        try {
            loadFromIntent();
        } catch (Exception exc) {
            Toast.makeText(this, "Something got wrong. Try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void loadFromIntent() throws NullPointerException, IOException {
        Intent intent = getIntent();
        int parent = intent.getIntExtra("parent", 0);

        switch (parent) {
            case ChaptersNotesViewHolder.FROM_CHAPTER: {
                Chapter chapter = (Chapter) intent.getSerializableExtra("chapter");
                parentId = chapter.getId();
                id = UUID.randomUUID().toString();
                break;
            }
            case ChaptersNotesViewHolder.FROM_NOTE: {
                Note note = (Note) intent.getSerializableExtra("note");
                currentMarkerType = Markers.incrementMarker(note.getMarkerType());
                parentId = note.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(note.getId()).observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if (notes.size() > 0) {
                            currentMarkerType = notes.get(0).getMarkerType();
                            markerTypeLocked = true;
                            setImageView();
                        }
                    }
                });
                break;
            }
            case ChaptersNotesViewHolder.EDITION: {
                Note note = (Note) intent.getSerializableExtra("note");
                add_note_name.setText(note.getNote());
                currentMarkerType = note.getMarkerType();
                parentId = note.getParentId();
                id = note.getId();
                inEdition = true;

                viewModel.getNotesByParent(note.getParentId()).observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if (notes.size() > 0) {
                            currentMarkerType = notes.get(0).getMarkerType();
                            markerTypeLocked = true;
                            setImageView();
                        }
                    }
                });
                break;
            }
            default: {
                throw new NullPointerException();
            }
        }
        setImageView();
    }

    private void initWidgets() {
        add_note_name = (EditText) findViewById(R.id.add_note_name);
        add_note_imgView = (ImageView) findViewById(R.id.add_note_imgView);
        add_note_imgView.setImageResource(R.drawable.color_dot);
        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {

        add_note_imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!markerTypeLocked) {
                    currentMarkerType = Markers.incrementMarker(currentMarkerType);
                    try {
                        if (currentMarkerType == Markers.NUMBER_MARKER || currentMarkerType == Markers.LETTER_MARKER) {
                            add_note_imgView.setImageDrawable(Markers.getLetterMarker(currentMarkerType, 0,
                                    Color.RED, TextDrawable.LARGE_TEXT_SIZE));
                        } else {
                            add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, Color.RED));
                        }
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {
                    if (inEdition) {
                        viewModel.update(thisNote);
                    } else {
                        viewModel.insert(thisNote);
                    }
                    finish();
                } else {
                    Toast.makeText(AddNoteActivity.this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean areValidOutputs() {
        String note = add_note_name.getText().toString();
        if (note.length() < 2) return false;

        thisNote = new Note(id, currentMarkerType, note, parentId);
        return true;
    }

    private void setImageView() {
        try {
            if (currentMarkerType == Markers.NUMBER_MARKER || currentMarkerType == Markers.LETTER_MARKER) {
                add_note_imgView.setImageDrawable(Markers.getLetterMarker(currentMarkerType, 0, Color.GREEN, TextDrawable.LARGE_TEXT_SIZE));
            } else {
                add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, Color.GREEN));
            }
        } catch (IOException exc){
            exc.printStackTrace();
        }
    }
}
