package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

public class ChaptersRecViewAdapter extends RecyclerView.Adapter<ChaptersRecViewAdapter.ViewHolder> {

    private List<Chapter> chapters = new ArrayList<>();

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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.listitem_chapter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(chapters.get(position));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    //Inner classes
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView chapter_name;
        private RecyclerView chapter_recView;
       // private NotesRecViewAdapter adapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chapter_name = (TextView) itemView.findViewById(R.id.chapter_name);
            chapter_recView = (RecyclerView) itemView.findViewById(R.id.chapter_recView);
        }

        public void setData(Chapter chapter) {
            chapter_name.setText(chapter.getName());

            try {
                NoteViewModel noteModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
                noteModel.getNotesByParent(chapter.getId()).observe((FragmentActivity) context, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        NotesRecViewAdapter adapter = new NotesRecViewAdapter(context);
                        adapter.setNotesList(notes);
                        chapter_recView.setAdapter(adapter);
                        chapter_recView.setLayoutManager(new GridLayoutManager(context,1));
                    }
                });
            } catch (NullPointerException exc) {
                exc.printStackTrace();
            }
        }
    }
}
