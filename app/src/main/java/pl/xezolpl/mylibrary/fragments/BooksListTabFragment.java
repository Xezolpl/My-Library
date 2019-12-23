package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.models.Categories;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;

public class BooksListTabFragment extends Fragment {

    private Context context;
    private int tabBooksStatus;

    private RecyclerView booksRecView;
    private BooksRecViewAdapter booksRecViewAdapter;

    private BookViewModel bookViewModel;
    private String categoryName = null;

    public BooksListTabFragment() {
        tabBooksStatus = Book.STATUS_NEUTRAL;
    }

    public BooksListTabFragment(int tabBooksStatus) {
        this.tabBooksStatus = tabBooksStatus;
    }

    public BooksListTabFragment(String categoryName) {
        this.categoryName = categoryName;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        booksRecViewAdapter = new BooksRecViewAdapter(context);

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        /*ALL BOOKS*/
        if (tabBooksStatus == Book.STATUS_NEUTRAL) {
            bookViewModel.getAllBooks().observe(this, new Observer<List<Book>>() {
                @Override
                public void onChanged(final List<Book> books) {
                    if (categoryName == null) {
                        booksRecViewAdapter.setBooks(books);
                    }else{
                        final List<Book> booksWithCategory = new ArrayList<>();

                        CategoriesViewModel categoriesViewModel = ViewModelProviders.of((FragmentActivity)context)
                                .get(CategoriesViewModel.class);

                        categoriesViewModel.getBooksByCategory(categoryName)
                                .observe((FragmentActivity) context, new Observer<List<Categories>>() {
                                    @Override
                                    public void onChanged(List<Categories> categories) {
                                        for (int i=0; i<categories.size(); i++){
                                            if(categories.contains(new Categories(books.get(i).getId()
                                                    ,categoryName))){
                                                booksWithCategory.add(books.get(i));
                                            }
                                        }
                                    }
                                });
                        booksRecViewAdapter.setBooks(booksWithCategory);
                    }
                }
            });
        }
        /*OTHER STATUS*/
        else {
            bookViewModel.getBookWithStatus(tabBooksStatus).observe(this, new Observer<List<Book>>() {
                @Override
                public void onChanged(List<Book> books) {
                    booksRecViewAdapter.setBooks(books);
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_books_list, container, false);

        booksRecView = (RecyclerView) view.findViewById(R.id.booksRecView);
        booksRecView.setAdapter(booksRecViewAdapter);
        booksRecView.setLayoutManager(new GridLayoutManager(context, 2));

        return view;
    }

    public void setFilter(String filter) {
        booksRecViewAdapter.getFilter().filter(filter);
    }
}
