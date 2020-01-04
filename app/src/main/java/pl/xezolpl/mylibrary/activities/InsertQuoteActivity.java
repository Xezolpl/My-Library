package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class InsertQuoteActivity extends AppCompatActivity {

    private RecyclerView recView;
    private Button okBtn, cancelBtn;

    private QuoteViewModel viewModel;
    private QuotesRecViewAdapter adapter;

    private Chapter chapter;
    private List<Quote> quotes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_quote);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initWidgets();
        setOnClickListeners();

        chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        adapter = new QuotesRecViewAdapter(this);
        adapter.setInserting(true);
        adapter.setChapterId(chapter.getId());

        recView.setLayoutManager(new GridLayoutManager(this, 1));

        viewModel = ViewModelProviders.of(this).get(QuoteViewModel.class);
        viewModel.getQuotesByBook(chapter.getBookId()).observe(this, new Observer<List<Quote>>() {
            @Override
            public void onChanged(List<Quote> quotes) {
                adapter.setQuotes(quotes);
                recView.setAdapter(adapter);
            }
        });
    }


    private void initWidgets() {
        recView = findViewById(R.id.recView);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
    }

    private void setOnClickListeners() {
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateChapterQuotes(adapter.getChapterQuotes());
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateChapterQuotes(List<Quote> quotes) {
        for (Quote quote : quotes) {
            viewModel.update(quote);
        }
    }
}
