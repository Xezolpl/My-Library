package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import pl.xezolpl.mylibrary.R;

public class MainActivity extends AppCompatActivity {

    private Button allBooksBtn, currReadingBtn, toReadBtn, alreadyReadBtn, aboutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        createOnClickListeners();
    }

    private void createOnClickListeners() {
        allBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AllBooksActivity.class);
                startActivity(intent);
            }
        });
    }

        private void initWidgets () {
            allBooksBtn = (Button) findViewById(R.id.allBooksBtn);
            currReadingBtn = (Button) findViewById(R.id.currReadingBtn);
            toReadBtn = (Button) findViewById(R.id.toReadBtn);
            alreadyReadBtn = (Button) findViewById(R.id.alreadyReadBtn);
            aboutBtn = (Button) findViewById(R.id.aboutBtn);
        }
    }