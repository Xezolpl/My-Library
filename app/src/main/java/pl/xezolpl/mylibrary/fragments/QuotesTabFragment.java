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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddQuoteActivity;
import pl.xezolpl.mylibrary.adapters.QuotesRecViewAdapter;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class QuotesTabFragment extends Fragment {
    private static final String TAG = "QuotesTabFragment";
    public static final int ADD_QUOTE_ACTIVITY_REQUEST_CODE=1;
    public static final int EDIT_QUOTE_ACTIVITY_REQUEST_CODE=2;
    public static final int ADD_CATEGORY_ACTIVITY_REQUEST_CODE=3;
    public static final int EDIT_CATEGORY_ACTIVITY_REQUEST_CODE=4;
    public static final int RESULT_DELETE=8;

    private Context context;
    private String bookId;

    private FloatingActionButton quotes_fab;
    private RecyclerView quotes_recView;

    private QuoteViewModel quoteViewModel;
    private QuotesRecViewAdapter quotesRecViewAdapter;


    public QuotesTabFragment(Context context, String bookId) {
        this.context = context;
        this.bookId = bookId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        quotesRecViewAdapter = new QuotesRecViewAdapter(context);

        quoteViewModel = ViewModelProviders.of(this).get(QuoteViewModel.class);
        if(bookId.isEmpty()){
            quoteViewModel.getAllQuotes().observe(this, new Observer<List<Quote>>() {
                @Override
                public void onChanged(List<Quote> quotes) {
                    quotesRecViewAdapter.setQuotes(quotes);
                }
            });
        }else {
            quoteViewModel.getQuotesByBook(bookId).observe(this, new Observer<List<Quote>>() {
                @Override
                public void onChanged(List<Quote> quotes) {
                    quotesRecViewAdapter.setQuotes(quotes);
                }
            });
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_quotes, container,false);


        quotes_recView = (RecyclerView) view.findViewById(R.id.quotes_recView);
        quotes_recView.setAdapter(quotesRecViewAdapter);
        quotes_recView.setLayoutManager(new GridLayoutManager(context,1));

        quotes_fab = (FloatingActionButton) view.findViewById(R.id.quotes_fab);
        quotes_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddQuoteActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        setMenuVisibility(false);

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
