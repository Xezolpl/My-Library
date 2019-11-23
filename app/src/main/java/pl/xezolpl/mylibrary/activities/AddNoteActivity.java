package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.ChaptersNotesViewHolder;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class AddNoteActivity extends AppCompatActivity {

    private EditText add_note_name;
    private ImageView add_note_imgView;
    private Button ok_btn, cancel_btn;

    private NoteViewModel viewModel;

    private int currentMarkerType = R.drawable.color_dot;
    private String parentId = "";
    private int request = 0;
    private String currentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initWidgets();
        setOnClickListeners();

        viewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        Intent intent = getIntent();

        if (intent.hasExtra("request")) {
            request = (intent.getIntExtra("request", 0));
        }

        try {
            Chapter chapter = (Chapter) intent.getSerializableExtra("chapter");
            currentMarkerType = R.drawable.color_dot;
            parentId = chapter.getId();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try {
            Note note = (Note) intent.getSerializableExtra("note");

            if(request == ChaptersNotesViewHolder.EDIT_REQUEST){
                add_note_name.setText(note.getNote());
                currentMarkerType  = note.getMarkerType();
                parentId = note.getParentId();
                currentId = note.getId();
            }

            else {
                currentMarkerType = note.getMarkerType();// + 1;
                parentId = note.getId();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    private void initWidgets() {
        add_note_name = (EditText) findViewById(R.id.add_note_name);
        add_note_imgView = (ImageView) findViewById(R.id.add_note_imgView);
        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {

        add_note_imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: CHANGE MARKER TYPE
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {
                    if(request == ChaptersNotesViewHolder.EDIT_REQUEST){
                        Note note = new Note(currentId,currentMarkerType,add_note_name.getText().toString(),parentId);
                        viewModel.update(note);
                    }
                    else{
                        Note note = new Note(UUID.randomUUID().toString(), currentMarkerType, add_note_name.getText().toString(), parentId);
                        viewModel.insert(note);
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
        return (add_note_name.getText().length() > 2);
    }
}
