package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.nikartm.button.FitButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.UUID;

import petrov.kristiyan.colorpicker.ColorPicker;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class AddNoteActivity extends AppCompatActivity {
    private static final String TAG = "AddNoteActivity";

    private MaterialEditText add_note_name;
    private ImageView add_note_imgView;
    private FitButton ok_btn, cancel_btn, add_note_color_btn;

    private int currentMarkerType = Markers.NUMBER_MARKER;
    private int color = Markers.BLUE_START_COLOR ;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setOnClickListeners();

        viewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        try {
            loadFromIntent();
        } catch (Exception exc) {
            Log.e(TAG, "onCreate: cannot read from intent!", exc);
            finish();
        }

    }

    private void loadFromIntent() throws NullPointerException {
        Intent intent = getIntent();
        int parent = intent.getIntExtra("parent", 0);

        switch (parent) {
            case 1: {
                final Chapter chapter = (Chapter) intent.getSerializableExtra("chapter");
                parentId = chapter.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(chapter.getId()).observe(this, notes -> {
                    if (notes.size() > 0) {
                        currentMarkerType = notes.get(0).getMarkerType();
                        markerTypeLocked = true;
                        color = notes.get(0).getColor();
                    } else {
                        color = Markers.BLUE_START_COLOR;
                    }
                    setImageView(color);
                });
                break;
            }
            case 2: {
                final Note note = (Note) intent.getSerializableExtra("note");
                currentMarkerType = Markers.incrementMarker(note.getMarkerType());
                parentId = note.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(note.getId()).observe(this, notes -> {
                    if (notes.size() > 0) {
                        currentMarkerType = notes.get(0).getMarkerType();
                        markerTypeLocked = true;
                        color = notes.get(0).getColor();
                    } else {
                        color = note.getColor();
                    }
                    setImageView(color);
                });
                break;
            }
            case 3: {
                final Note note = (Note) intent.getSerializableExtra("note");
                add_note_name.setText(note.getNote());
                currentMarkerType = note.getMarkerType();
                parentId = note.getParentId();
                id = note.getId();
                color = note.getColor();
                inEdition = true;

                viewModel.getNotesByParent(note.getParentId()).observe(this, notes -> {
                    if (notes.size() > 1) {
                        currentMarkerType = notes.get(0).getMarkerType();
                        markerTypeLocked = true;
                        setImageView(color);
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
                    0, Markers.BLUE_START_COLOR));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ok_btn = findViewById(R.id.ok_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        add_note_color_btn = findViewById(R.id.add_note_color_btn);
    }

    private void setOnClickListeners() {

        add_note_imgView.setOnClickListener(view -> {
            if (!markerTypeLocked) {
                currentMarkerType = Markers.incrementMarker(currentMarkerType);
                try {
                    if (currentMarkerType == Markers.NUMBER_MARKER || currentMarkerType == Markers.LETTER_MARKER) {
                        add_note_imgView.setImageDrawable(Markers.getLetterMarker(currentMarkerType, 0,
                                color));
                    } else {
                        add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, color));
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        });

        ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                if (inEdition) {
                    viewModel.update(thisNote);
                } else {
                    viewModel.insert(thisNote);
                }
                setResult(RESULT_OK, new Intent().putExtra("note", thisNote));
                finish();
            } else {
                Toast.makeText(AddNoteActivity.this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        cancel_btn.setOnClickListener(view -> finish());

        add_note_color_btn.setOnClickListener(view -> {
            ColorPicker picker = new ColorPicker(AddNoteActivity.this);
            picker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                @Override
                public void onChooseColor(int position, int color) {
                    add_note_imgView.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    add_note_imgView.invalidate();
                    AddNoteActivity.this.color = color;
                }

                @Override
                public void onCancel() {
                }
            }).show();
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
                add_note_imgView.setImageDrawable(Markers.getLetterMarker(currentMarkerType, 0, markerColor));
            } else {
                add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, currentMarkerType, markerColor));
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }



}
