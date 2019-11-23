package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.dialogs.AboutDialog;

public class MainActivity extends AppCompatActivity {

    private Button open_all_books_btn, about_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        createOnClickListeners();

    }//ACTUALLY DISABLED

    private void createOnClickListeners() {
        open_all_books_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllBooksActivity.class);
                startActivity(intent);
            }
        });

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "About application");
            }
        });
    }

        private void initWidgets () {
            open_all_books_btn = (Button) findViewById(R.id.open_all_books_btn);
            about_btn = (Button) findViewById(R.id.about_btn);
        }
    }