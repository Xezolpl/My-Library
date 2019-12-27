package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import petrov.kristiyan.colorpicker.ColorPicker;
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
    private Button ok_btn, cancel_btn, add_note_color_btn;

    private int currentMarkerType = Markers.NUMBER_MARKER;
    private int color = Color.BLUE;

    private String parentId;
    private String id;

    private boolean inEdition = false;
    private boolean markerTypeLocked = false;

    private Note thisNote;
    private NoteViewModel viewModel;

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

    private void loadFromIntent() throws NullPointerException {
        Intent intent = getIntent();
        int parent = intent.getIntExtra("parent", 0);

        switch (parent) {
            case ChaptersNotesViewHolder.FROM_CHAPTER: {
                final Chapter chapter = (Chapter) intent.getSerializableExtra("chapter");
                parentId = chapter.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(chapter.getId()).observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if (notes.size() > 0) {
                            currentMarkerType = notes.get(0).getMarkerType();
                            markerTypeLocked = true;
                            color = notes.get(0).getColor();
                        } else {
                            color = Markers.BLUE_START_COLOR;
                        }
                        setImageView(color);
                    }
                });
                break;
            }
            case ChaptersNotesViewHolder.FROM_NOTE: {
                final Note note = (Note) intent.getSerializableExtra("note");
                currentMarkerType = Markers.incrementMarker(note.getMarkerType());
                parentId = note.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(note.getId()).observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if (notes.size() > 0) {
                            currentMarkerType = notes.get(0).getMarkerType();
                            markerTypeLocked = true;
                            color = notes.get(0).getColor();
                        } else {
                            color = note.getColor();
                        }
                        setImageView(color);
                    }
                });
                break;
            }
            case ChaptersNotesViewHolder.EDITION: {
                final Note note = (Note) intent.getSerializableExtra("note");
                add_note_name.setText(note.getNote());
                currentMarkerType = note.getMarkerType();
                parentId = note.getParentId();
                id = note.getId();
                color = note.getColor();
                inEdition = true;

                viewModel.getNotesByParent(note.getParentId()).observe(this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if (notes.size() > 1) {
                            currentMarkerType = notes.get(0).getMarkerType();
                            markerTypeLocked = true;
                            setImageView(color);
                        }
                    }
                });
                break;
            }
            default: {
                throw new NullPointerException();
            }
        }
        setImageView(color);
    }

    private void initWidgets() {
        add_note_name = findViewById(R.id.add_note_name);
        add_note_imgView = findViewById(R.id.add_note_imgView);
        try {
            add_note_imgView.setImageDrawable(Markers.getLetterMarker(Markers.NUMBER_MARKER,
                    0, Color.BLUE, TextDrawable.LARGE_TEXT_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ok_btn = findViewById(R.id.ok_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        add_note_color_btn = findViewById(R.id.add_note_color_btn);
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
                                    color, TextDrawable.LARGE_TEXT_SIZE));
                        } else {
                            add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, color));
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

        add_note_color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(AddNoteActivity.this);
                picker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        add_note_imgView.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        AddNoteActivity.this.color = color;
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
            }
        });
    }

    private boolean areValidOutputs() {
        String note = add_note_name.getText().toString();
        if (note.length() < 2) return false;

        thisNote = new Note(id, currentMarkerType, note, parentId, color);
        return true;
    }

    private void setImageView(int markerColor) {
        try {
            if (currentMarkerType == Markers.NUMBER_MARKER || currentMarkerType == Markers.LETTER_MARKER) {
                add_note_imgView.setImageDrawable(Markers.getLetterMarker(currentMarkerType, 0, markerColor, TextDrawable.LARGE_TEXT_SIZE));
            } else {
                add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, markerColor));
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
