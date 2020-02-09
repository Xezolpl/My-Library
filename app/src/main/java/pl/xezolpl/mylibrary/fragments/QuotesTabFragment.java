package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddQuoteActivity;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
import pl.xezolpl.mylibrary.managers.LinearLayoutManagerWrapper;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class QuotesTabFragment extends Fragment {

    private Context context;
    private String bookId;

    private QuotesRecViewAdapter quotesRecViewAdapter;
    private RecyclerView quotes_recView =  null;

    private Quote latestQuote = null;

    public QuotesTabFragment(Context context, String bookId) {
        this.context = context;
        this.bookId = bookId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quotesRecViewAdapter = new QuotesRecViewAdapter(context);

        QuoteViewModel quoteViewModel = ViewModelProviders.of(this).get(QuoteViewModel.class);
        if (bookId.isEmpty()) {
            quoteViewModel.getAllQuotes().observe(this, quotes ->{
                quotesRecViewAdapter.setQuotes(quotes);
                latestQuote = quotes.get(quotes.size()-1);
                if(quotes_recView!=null) quotes_recView.invalidate();
            });
        } else {
            quoteViewModel.getQuotesByBook(bookId).observe(this, quotes -> {
                quotesRecViewAdapter.setQuotes(quotes);
                latestQuote = quotes.get(quotes.size()-1);
                if(quotes_recView!=null) quotes_recView.invalidate();

            });
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_quotes, container, false);


        quotes_recView = view.findViewById(R.id.quotes_recView);
        quotes_recView.setAdapter(quotesRecViewAdapter);
        quotes_recView.setLayoutManager(new LinearLayoutManagerWrapper(context));

        FloatingActionButton fab = view.findViewById(R.id.quotes_fab);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, AddQuoteActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("latestQuote", latestQuote);
            context.startActivity(intent);
        });
        setHasOptionsMenu(true);
        if (!bookId.isEmpty())setMenuVisibility(false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.quotes_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.quotes_searchView);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                quotesRecViewAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}
