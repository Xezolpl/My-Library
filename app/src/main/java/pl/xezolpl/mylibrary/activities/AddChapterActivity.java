package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.nikartm.button.FitButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;

public class AddChapterActivity extends AppCompatActivity {

    private MaterialEditText add_chapter_name, add_chapter_number;
    private FitButton ok_btn, cancel_btn;

    private ChapterViewModel viewModel;

    private String bookId;
    private boolean inEdition = false;
    private Chapter thisChapter = null;

    private int backCounter =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadDialogTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setOnClickListeners();

        viewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        Intent intent = getIntent();

        if (intent.hasExtra("chapter")) {
            thisChapter = (Chapter) intent.getSerializableExtra("chapter");
            inEdition = true;

            add_chapter_name.setText(thisChapter.getName());
            add_chapter_number.setText(String.valueOf(thisChapter.getNumber()));
        }
            bookId = intent.getStringExtra("bookId");
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

        if (inEdition){
            id = thisChapter.getId();
            bookId = thisChapter.getBookId();

        }
        else id = UUID.randomUUID().toString();


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
}
