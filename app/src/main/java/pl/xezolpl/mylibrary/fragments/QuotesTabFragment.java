package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddQuoteActivity;
import pl.xezolpl.mylibrary.adapters.QuoteCategorySpinnerAdapter;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
import pl.xezolpl.mylibrary.managers.LinearLayoutManagerWrapper;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class QuotesTabFragment extends Fragment {

    private Context context;
    private String bookId;

    private QuotesRecViewAdapter quotesRecViewAdapter;
    private RecyclerView quotes_recView = null;

    private Quote latestQuote = null;

    private QuoteCategorySpinnerAdapter categoriesAdapter;

    public QuotesTabFragment(Context context, String bookId) {
        this.context = context;
        this.bookId = bookId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quotesRecViewAdapter = new QuotesRecViewAdapter(context);

        QuoteViewModel quoteViewModel = new ViewModelProvider(this).get(QuoteViewModel.class);
        if (bookId.isEmpty()) { // Quotes of the specific book (we are inside the book)
            quoteViewModel.getAllQuotes().observe(this, quotes -> {
                if (quotes.size() > 0) {
                    quotesRecViewAdapter.setQuotes(quotes);
                    latestQuote = quotes.get(quotes.size() - 1);
                    if (quotes_recView != null) quotes_recView.invalidate();
                }
            });
        } else { // All quotes by the drawer
            quoteViewModel.getQuotesByBook(bookId).observe(this, quotes -> {
                if (quotes.size() > 0) {
                    quotesRecViewAdapter.setQuotes(quotes);
                    latestQuote = quotes.get(quotes.size() - 1);
                    if (quotes_recView != null) quotes_recView.invalidate();
                }
            });
        }

        categoriesAdapter = new QuoteCategorySpinnerAdapter(context);
        TypedValue typedValue = new TypedValue();

        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        categoriesAdapter.setItemBackgroundColor(typedValue.data);

        context.getTheme().resolveAttribute(R.attr.textColorOnPrimaryColor, typedValue, true);
        categoriesAdapter.setItemTextColor(typedValue.data);

        QuoteCategoryViewModel qcvm = new ViewModelProvider(this).get(QuoteCategoryViewModel.class);
        qcvm.getAllCategories().observe(this, quoteCategories -> {
            quoteCategories.add(0, new QuoteCategory("",
                    getString(R.string.all_quote_categories), Markers.BLUE_START_COLOR));
            categoriesAdapter.setCategories(quoteCategories);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotes, container, false);

        quotes_recView = view.findViewById(R.id.quotes_recView);
        quotes_recView.setAdapter(quotesRecViewAdapter);
        quotes_recView.setLayoutManager(new LinearLayoutManagerWrapper(context));

        FloatingActionButton fab = view.findViewById(R.id.quotes_fab);
        fab.setOnClickListener(view1 -> { // Add new quote
            Intent intent = new Intent(context, AddQuoteActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("latestQuote", latestQuote);
            context.startActivity(intent);
        });
        setHasOptionsMenu(true);
        if (!bookId.isEmpty()) setMenuVisibility(false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.quotes_menu, menu);

        //Filtering mechanic
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

        MenuItem statusItem = menu.findItem(R.id.quotes_status);
        Spinner spinner = (Spinner) statusItem.getActionView();
        (new Handler()).postDelayed(()->{
            spinner.setAdapter(categoriesAdapter);
            spinner.setSelection(0);
        }, 200);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                QuoteCategory category = (QuoteCategory)categoriesAdapter.getItem(i);
                quotesRecViewAdapter.setCategoryFilter(category.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
