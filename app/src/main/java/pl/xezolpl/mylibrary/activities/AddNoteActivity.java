package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikartm.button.FitButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import petrov.kristiyan.colorpicker.ColorPicker;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

@SuppressLint("SourceLockedOrientationActivity")
public class AddNoteActivity extends AppCompatActivity {
    private static final String TAG = "AddNoteActivity";

    public static final int PARENT_CHAPTER = 1;
    public static final int PARENT_NOTE = 2;
    public static final int EDITION = 3;

    private EditText add_note_name;
    private ImageView add_note_imgView;
    private FitButton ok_btn, cancel_btn, add_note_color_btn;

    private int markerPosition = 0;
    private int markerType = Markers.NUMBER_MARKER;
    private int markerColor = Markers.BLUE_START_COLOR;

    private String parentId;
    private String id;

    private boolean inEdition = false;
    private boolean markerTypeLocked = false;

    private Note thisNote;
    private NoteViewModel viewModel;

    private int backCounter = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SettingsManager manager = new SettingsManager(this);
        manager.loadDialogTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_note);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);

        initWidgets();
        setOnClickListeners();

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        try {
            loadFromIntent();
        } catch (Exception exc) {
            Toast.makeText(this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onCreate: cannot read from intent!", exc);
            finish();
        }
        (new Handler()).postDelayed(()->{
            if(manager.isRandomColorPickingEnabled() && !inEdition){
                markerColor = randomPickerColor();
                setImageView(markerColor);
            }
        }, 10);
    }

    /**
     * Sets all needed variables basing on this note's parent given by the calling intent,
     * then sets the imageView with adequate markerType and markerColor
     * @throws NullPointerException when the parent is incorrect
     */
    private void loadFromIntent() throws NullPointerException {
        Intent intent = getIntent();
        int parent = intent.getIntExtra("parent", 0);

        switch (parent) {
            case PARENT_CHAPTER: {
                final Chapter chapter = (Chapter) intent.getSerializableExtra("chapter");
                parentId = chapter.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(chapter.getId()).observe(this, notes -> {
                    if (notes.size() > 0) {
                        markerPosition = notes.size();
                        markerType = notes.get(0).getMarkerType();
                        markerTypeLocked = true;
                        markerColor = notes.get(notes.size() - 1).getColor();
                    }
                    setImageView(markerColor);
                });
                break;
            }
            case PARENT_NOTE: {
                final Note note = (Note) intent.getSerializableExtra("note");
                markerType = Markers.incrementMarker(note.getMarkerType());
                parentId = note.getId();
                id = UUID.randomUUID().toString();

                viewModel.getNotesByParent(note.getId()).observe(this, notes -> {
                    if (notes.size() > 0) {
                        markerPosition = notes.size();
                        markerType = notes.get(0).getMarkerType();
                        markerTypeLocked = true;
                        markerColor = notes.get(notes.size() - 1).getColor();
                    } else {
                        markerColor = note.getColor();
                    }
                    setImageView(markerColor);
                });
                break;
            }
            case EDITION: {
                final Note note = (Note) intent.getSerializableExtra("note");
                add_note_name.setText(note.getNote());

                markerType = note.getMarkerType();
                markerColor = note.getColor();

                id = note.getId();
                parentId = note.getParentId();

                inEdition = true;

                viewModel.getNotesByParent(note.getParentId()).observe(this, notes -> {
                    if (notes.size() > 1) {
                        markerPosition = notes.size()-1;
                        markerTypeLocked = true;
                        setImageView(markerColor);
                    }
                });
                break;
            }
            default: {
                throw new NullPointerException();
            }
        }
        setImageView(markerColor);
    }

    private void initWidgets() {
        add_note_name = findViewById(R.id.add_note_name);
        add_note_imgView = findViewById(R.id.add_note_imgView);
        ok_btn = findViewById(R.id.ok_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        add_note_color_btn = findViewById(R.id.add_note_color_btn);
    }

    private void setOnClickListeners() {

        add_note_imgView.setOnClickListener(view -> {
            //markerType is locked when this note isn't the first note of its parent - then we can't change the markerType
            if (!markerTypeLocked) {
                markerType = Markers.incrementMarker(markerType); // markerType +1 (basing on specific ids)
                try {
                    if (markerType == Markers.NUMBER_MARKER || markerType == Markers.LETTER_MARKER) {
                        add_note_imgView.setImageDrawable(Markers
                                .getLetterMarker(markerType, markerPosition, markerColor));
                    } else {
                        add_note_imgView.setImageDrawable(Markers
                                .getSimpleMarker(this, markerType, markerColor));
                    }
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.cant_change_not_first_note), Toast.LENGTH_SHORT).show();
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
            }
        });

        cancel_btn.setOnClickListener(view -> finish());

        add_note_color_btn.setOnClickListener(view -> {
            ColorPicker picker = new ColorPicker(AddNoteActivity.this);
            picker.setColors(getResources().getIntArray(R.array.default_colors));
            picker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                @Override
                public void onChooseColor(int position, int color) { //sets picked color on the add_note_imgView
                    add_note_imgView.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                    add_note_imgView.invalidate();
                    markerColor = color;
                }

                @Override
                public void onCancel() {
                }
            }).show();
        });
    }

    private boolean areValidOutputs() {
        String note = add_note_name.getText().toString();
        if (note.length() < 2){
            Toast.makeText(this, getString(R.string.too_short), Toast.LENGTH_SHORT).show();
            return false;
        }

        thisNote = new Note(id, markerType, note, parentId, markerColor);
        return true;
    }

    /**
     * Sets the marker's imgView from basing on the markerType
     */
    private void setImageView(int markerColor) {
        try {
            if (markerType == Markers.NUMBER_MARKER || markerType == Markers.LETTER_MARKER) {
                add_note_imgView.setImageDrawable(Markers.getLetterMarker(markerType, markerPosition, markerColor));
            } else {
                add_note_imgView.setImageDrawable(Markers.getSimpleMarker(AddNoteActivity.this, markerType, markerColor));
            }
        } catch (IOException exc) { // Markers.get?Marker() throws IOException if the input was incorrect
            exc.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCounter == 0){
                backCounter = 1;
                (new Handler()).postDelayed(()->backCounter=0, 2000);
            } else {
                backCounter = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    private int randomPickerColor(){
        int[] colors = getResources().getIntArray(R.array.default_colors);
        int random = new Random().nextInt(colors.length);
        return colors[random];
    }

}
