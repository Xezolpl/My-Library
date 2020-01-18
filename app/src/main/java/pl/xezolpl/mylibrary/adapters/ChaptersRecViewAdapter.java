package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Chapter;

public class ChaptersRecViewAdapter extends RecyclerView.Adapter<ChaptersNotesViewHolder> {

    private List<Chapter> chapters = new ArrayList<>();
    private List<ChaptersNotesViewHolder> viewHolders = new ArrayList<>();

    private Context context;
    private LayoutInflater inflater;

    public ChaptersRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setChaptersList(List<Chapter> chapters) {
        this.chapters = chapters;
        notifyDataSetChanged();
    }

    public List<ChaptersNotesViewHolder> getViewHolders() {
        return viewHolders;
    }

    @NonNull
    @Override
    public ChaptersNotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.listitem_chapter, parent, false);
        ChaptersNotesViewHolder holder = new ChaptersNotesViewHolder(v, context, ChaptersNotesViewHolder.FROM_CHAPTER);
        viewHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersNotesViewHolder holder, int position) {
        holder.setData(chapters.get(position));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }
}