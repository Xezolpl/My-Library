package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_quote);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setOnClickListeners();

        Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        adapter = new QuotesRecViewAdapter(this);
        adapter.setInserting(true);
        adapter.setChapterId(chapter.getId());

        recView.setLayoutManager(new GridLayoutManager(this, 1));
        recView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(QuoteViewModel.class);
        viewModel.getQuotesByBook(chapter.getBookId()).observe(this, quotes -> {
            adapter.setQuotes(quotes);
        });
    }


    private void initWidgets() {
        recView = findViewById(R.id.recView);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {
        okBtn.setOnClickListener(view -> {
            updateChapterQuotes(adapter.getChapterQuotes());
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
