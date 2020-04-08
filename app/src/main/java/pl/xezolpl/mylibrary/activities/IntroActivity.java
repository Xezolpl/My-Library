package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.IntroPagerAdapter;
import pl.xezolpl.mylibrary.managers.SettingsManager;

public class IntroActivity extends AppCompatActivity {
    private ViewPager intro_viewPager;
    private IntroPagerAdapter adapter;

    private Button prevBtn, nextBtn;
    private LinearLayout dotsLayout;

    private int backCounter = 0;

    @ColorInt private int colorAccent, colorAccentTransparent;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initWidgets();
        setListeners();

        adapter = new IntroPagerAdapter(this);
        intro_viewPager.setAdapter(adapter);

        //Get needed colours (depending on which theme is currently in use).
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        colorAccent = typedValue.data;

        getTheme().resolveAttribute(R.attr.colorAccentTransparent, typedValue, true);
        colorAccentTransparent = typedValue.data;

        addDotsIndicator(0);
    }

    private void initWidgets(){
        intro_viewPager = findViewById(R.id.intro_viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
    }

    private void setListeners(){
        intro_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);

                if (position == 0) { //first slide
                    prevBtn.setVisibility(View.GONE);
                } else if (position == 9) { //last slide
                    nextBtn.setText(getString(R.string.finish));
                } else { //other slides
                    prevBtn.setVisibility(View.VISIBLE);
                    nextBtn.setText(getString(R.string.next));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        prevBtn.setOnClickListener(view -> intro_viewPager.setCurrentItem(intro_viewPager.getCurrentItem()-1));

        nextBtn.setOnClickListener(view -> {
            int nextSlide = intro_viewPager.getCurrentItem()+1;
            if (nextSlide == adapter.getCount()){
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isIntroOpenedBefore", true).apply();
                finish();
            }
            else{
                intro_viewPager.setCurrentItem(nextSlide);
            }
        });
    }

    private void addDotsIndicator(int position) {

        TextView[] dots = new TextView[adapter.getCount()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorAccentTransparent);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(colorAccent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (backCounter == 0) { //first backClick - the second time application will be closed (after 2s counter resets)
                backCounter = 1;
                (new Handler()).postDelayed(() -> backCounter = 0, 2000);
            } else {
                backCounter = 0;
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isIntroOpenedBefore", true).apply();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}