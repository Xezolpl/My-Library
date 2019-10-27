package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Book;

public class BookNotesTabFragment extends Fragment {

    private Toolbar book_notes_toolbar;
    private Context context;
    private Book thisBook;

    public BookNotesTabFragment(Book thisBook, Context context) {
        this.thisBook = thisBook;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_book_notes ,container,false);
        initWidgets(view);
        return view;
    }

    private void initWidgets(View v){
        book_notes_toolbar = (Toolbar) v.findViewById(R.id.book_notes_toolbar);
    }
}
