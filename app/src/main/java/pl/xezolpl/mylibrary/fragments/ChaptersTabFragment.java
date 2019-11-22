package pl.xezolpl.mylibrary.fragments;

import android.app.Activity;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddChapterActivity;
import pl.xezolpl.mylibrary.adapters.ChaptersRecViewAdapter;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class ChaptersTabFragment extends Fragment {
    private RecyclerView recView;
    private FloatingActionButton fab;

    private ChapterViewModel chapterViewModel;
    private NoteViewModel noteViewModel;

    private String bookId;
    private Context context;
    private Activity activity;

    private ChaptersRecViewAdapter adapter;

    public ChaptersTabFragment(Context context, String bookId) {
        this.bookId = bookId;
        this.context = context;
        this.activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ChaptersRecViewAdapter(context);

        chapterViewModel = ViewModelProviders.of(this).get(ChapterViewModel.class);
        chapterViewModel.getChaptersByBook(bookId).observe(this, new Observer<List<Chapter>>() {
            @Override
            public void onChanged(List<Chapter> chapters) {
                adapter.setChaptersList(chapters);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_chapters, container, false);

        recView = (RecyclerView) view.findViewById(R.id.chapters_recView);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new GridLayoutManager(context,1));


        fab = (FloatingActionButton) view.findViewById(R.id.chapters_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddChapterActivity.class);
                intent.putExtra("bookId",bookId);
                startActivity(intent);
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chapters_menu, menu);
    }
}
