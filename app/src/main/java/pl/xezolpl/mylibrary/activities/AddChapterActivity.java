package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikartm.button.FitButton;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;

public class AddChapterActivity extends AppCompatActivity {

    private EditText add_chapter_name, add_chapter_number;
    private FitButton ok_btn, cancel_btn;

    private ChapterViewModel viewModel;

    private String bookId;
    private boolean inEdition = false;
    private Chapter thisChapter = null;
    private int backCounter = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadDialogTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_chapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);

        initWidgets();
        setOnClickListeners();

        viewModel = new ViewModelProvider(this).get(ChapterViewModel.class);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        if (intent.hasExtra("chapter")) {
            thisChapter = (Chapter) intent.getSerializableExtra("chapter");
            inEdition = true;

            //load chapter's data
            add_chapter_name.setText(thisChapter.getName());
            add_chapter_number.setText(String.valueOf(thisChapter.getNumber()));
        } else { //If its new chapter - display proposalNumber (one more than last chapter's number)
            viewModel.getChaptersByBook(bookId).observe(this, chapters -> {
                int proposalNumber = 0;
                if (chapters.size() > 0) {
                    proposalNumber = chapters.get(chapters.size() - 1).getNumber() + 1;
                }
                add_chapter_number.setText(String.valueOf(proposalNumber));
            });
        }


    }

    private void initWidgets() {
        add_chapter_name = findViewById(R.id.add_chapter_name);
        add_chapter_number = findViewById(R.id.add_chapter_number);
        ok_btn = findViewById(R.id.ok_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {
        ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                if (inEdition) {
                    viewModel.update(thisChapter);
                } else {
                    viewModel.insert(thisChapter);
                }
                finish();
            }
        });

        cancel_btn.setOnClickListener(view -> finish());
    }

    private boolean areValidOutputs() {
        String name = add_chapter_name.getText().toString();
        String id;
        int number;

        if (inEdition) {
            id = thisChapter.getId();
            bookId = thisChapter.getBookId();
        } else {
            id = UUID.randomUUID().toString();
        }

        try {
            number = Integer.valueOf(add_chapter_number.getText().toString());
        } catch (NumberFormatException exc) {
            Toast.makeText(this, getString(R.string.type_it_as_a_number), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.length() < 1) {
            Toast.makeText(this, getString(R.string.chapter_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        thisChapter = new Chapter(id, number, name, bookId);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCounter == 0) {
                backCounter = 1;
                (new Handler()).postDelayed(() -> backCounter = 0, 2000);
            } else {
                backCounter = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
