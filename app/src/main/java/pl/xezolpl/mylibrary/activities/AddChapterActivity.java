package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.ChaptersNotesViewHolder;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;

public class AddChapterActivity extends AppCompatActivity {

    private EditText add_chapter_name, add_chapter_number;
    private Button ok_btn, cancel_btn;

    private ChapterViewModel viewModel;
    private Chapter chapter;
    private int request = 0;
    private String bookId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        initWidgets();
        setOnClickListeners();

        viewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        Intent intent = getIntent();

        if (intent.hasExtra("request")) {
            request = intent.getIntExtra("request", 0);
        }

        try {
            chapter = (Chapter) intent.getSerializableExtra("chapter");
            if(request == ChaptersNotesViewHolder.EDIT_REQUEST){
                add_chapter_name.setText(chapter.getName());
                add_chapter_number.setText(String.valueOf(chapter.getNumber()));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Toast.makeText(this, "Something got wrong, try again.", Toast.LENGTH_SHORT).show();
        }

        try {
           bookId = intent.getStringExtra("bookId");
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }

    private void initWidgets() {
        add_chapter_name = (EditText) findViewById(R.id.add_chapter_name);
        add_chapter_number = (EditText) findViewById(R.id.add_chapter_number);
        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {
                    if(request==ChaptersNotesViewHolder.EDIT_REQUEST){
                        viewModel.update(new Chapter(chapter.getId(),
                                Integer.valueOf(add_chapter_number.getText().toString()),
                                add_chapter_name.getText().toString(),
                                chapter.getBookId()));
                    }
                    else {
                        viewModel.insert(new Chapter(UUID.randomUUID().toString(),
                                Integer.valueOf(add_chapter_number.getText().toString()),
                                add_chapter_name.getText().toString(), bookId));
                    }
                    finish();
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
        int number = 0;
        String name = add_chapter_name.getText().toString();

        try {
            number = Integer.valueOf(add_chapter_number.getText().toString());
        } catch (NumberFormatException exc) {
            Toast.makeText(this, "Write it as natural number. (Bigger than 0).", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (number < 0) {
            Toast.makeText(this, "Chapter's number can not be minus!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.length() < 3) {
            Toast.makeText(this, "Chapter's name can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
