package pl.xezolpl.mylibrary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.adapters.CategoryRecViewAdapter;

public class CategoriesFragment extends Fragment {

    private CategoryRecViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new CategoryRecViewAdapter(getContext(), CategoryRecViewAdapter.NORMAL_MODE, null);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView recView = view.findViewById(R.id.recView);
        recView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recView.setAdapter(adapter);

        return view;
    }

    public CategoryRecViewAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }
}
