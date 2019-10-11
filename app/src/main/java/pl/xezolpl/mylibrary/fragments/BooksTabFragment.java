package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.ViewModels.BookViewModel;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.models.Book;

public class BooksTabFragment extends Fragment {

    private Context context;
    private RecyclerView booksRecView;
    private int tabBooksStatus;
    private BooksRecViewAdapter adapter;
    private BookViewModel bookViewModel;

    public BooksTabFragment(Context context, int tabBooksStatus) {
        super();
        this.tabBooksStatus = tabBooksStatus;
        this.context = context;
        adapter = new BooksRecViewAdapter(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_tab_fragment, container, false);

        //display books
        booksRecView = (RecyclerView) view.findViewById(R.id.booksRecView);
        booksRecView.setAdapter(adapter);
        booksRecView.setLayoutManager(new GridLayoutManager(context, 2));

        //fill lists with books
        List<Book> books;

        switch (tabBooksStatus) {
            default:
                books = bookViewModel.getAllBooks().getValue();
                break;
        }
        adapter.setBooks(books);

        return view;
    }

    public BooksRecViewAdapter getAdapter() {
        return adapter;
    }
}
