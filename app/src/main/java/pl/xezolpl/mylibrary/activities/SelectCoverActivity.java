package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.xezolpl.mylibrary.R;

public class SelectCoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cover);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initWidgets();
        setOnClickListeners();
    }

    private void initWidgets() {

    }

    private void setOnClickListeners() {

    }

    private boolean isCoverSelected() {
        return true;
    }
}
