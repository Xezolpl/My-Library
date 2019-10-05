package pl.xezolpl.mylibrary.adapters;

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

import java.util.ArrayList;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.BookModel;
import pl.xezolpl.mylibrary.models.Status;
import pl.xezolpl.mylibrary.utlis.MyUtil;

public class BooksTabFragment extends Fragment {
    private Context context;
    private RecyclerView booksRecView;
    private Status status;
    private BooksRecViewAdapter adapter;


    public BooksTabFragment(Context context, Status status) {
        super();
        this.status = status;
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
        MyUtil myUtil = new MyUtil();
        ArrayList<BookModel> books;

        switch (status) {
            case NEUTRAL: {
                books = myUtil.getAllBooks();
                break;
            }
            case WANT_TO_READ: {
                books = myUtil.getWantToReadBooks();
                break;
            }
            case CURRENTLY_READING: {
                books = myUtil.getCurrentlyReadingBooks();
                break;
            }
            case ALREADY_READ: {
                books = myUtil.getAlreadyReadBooks();
                break;
            }
            default:
                books = myUtil.getAllBooks();
                break;
        }
        adapter.setBooks(books);

        return view;
    }

    public BooksRecViewAdapter getAdapter() {
        return adapter;
    }
}
