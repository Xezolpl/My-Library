package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddChapterActivity;
import pl.xezolpl.mylibrary.adapters.ChaptersRecViewAdapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;

public class ChaptersTabFragment extends Fragment {

    private String bookId;
    private Context context;

    private ChaptersRecViewAdapter adapter;

    ChaptersTabFragment(Context context, String bookId) {
        this.bookId = bookId;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ChaptersRecViewAdapter(context);

        ChapterViewModel chapterViewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        chapterViewModel.getChaptersByBook(bookId).observe(this, chapters -> adapter.setChaptersList(chapters));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_chapters, container, false);

        RecyclerView recView = view.findViewById(R.id.chapters_recView);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new GridLayoutManager(context, 1));


        FloatingActionButton fab = view.findViewById(R.id.chapters_fab);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, AddChapterActivity.class);
            intent.putExtra("bookId", bookId);
            startActivity(intent);
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chapters_menu, menu);
    }
}
