package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
import pl.xezolpl.mylibrary.managers.LinearLayoutManagerWrapper;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class InsertQuoteActivity extends AppCompatActivity {

    private RecyclerView recView;
    private FitButton okBtn, cancelBtn;

    private QuoteViewModel viewModel;
    private QuotesRecViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadDialogTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insert_quote);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);

        initWidgets();
        setOnClickListeners();

        Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        adapter = new QuotesRecViewAdapter(this);
        adapter.setInserting(true);
        adapter.setChapterId(chapter.getId());

        recView.setLayoutManager(new LinearLayoutManagerWrapper(this));
        recView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(QuoteViewModel.class);
        viewModel.getQuotesByBook(chapter.getBookId()).observe(this, quotes -> adapter.setQuotes(quotes));
    }

    private void initWidgets() {
        recView = findViewById(R.id.recView);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {
        okBtn.setOnClickListener(view -> {
            updateChapterQuotes(adapter.getChapterQuotes()); // we were changing the chapterIds of quotes
            finish();
        });
        cancelBtn.setOnClickListener(view -> finish());
    }

    private void updateChapterQuotes(List<Quote> quotes) {
        for (Quote quote : quotes) {
            viewModel.update(quote);
        }
    }
}
