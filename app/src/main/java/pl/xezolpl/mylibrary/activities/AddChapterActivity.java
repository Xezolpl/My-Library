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
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;

public class AddChapterActivity extends AppCompatActivity {

    private EditText add_chapter_name, add_chapter_number;
    private Button ok_btn, cancel_btn;

    private ChapterViewModel viewModel;

    private String bookId;
    private boolean inEdition = false;
    private Chapter thisChapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        initWidgets();
        setOnClickListeners();

        viewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        Intent intent = getIntent();

        if(intent.hasExtra("chapter")){
            thisChapter = (Chapter) intent.getSerializableExtra("chapter");
            inEdition = true;

            add_chapter_name.setText(thisChapter.getName());
            add_chapter_number.setText(String.valueOf(thisChapter.getNumber()));
        }

        bookId = intent.getStringExtra("bookId");

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
                    if (inEdition) {
                        viewModel.update(thisChapter);
                    } else {
                        viewModel.insert(thisChapter);
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
        String name = add_chapter_name.getText().toString();
        String id;
        int number;

        if (inEdition) id = thisChapter.getId();
        else id = UUID.randomUUID().toString();


        try {
            number = Integer.valueOf(add_chapter_number.getText().toString());
        } catch (NumberFormatException exc) {
            Toast.makeText(this, "Write it as natural number.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.length() < 1) {
            Toast.makeText(this, "Chapter's name can not be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        thisChapter = new Chapter(id,number,name,bookId);
        return true;
    }
}
