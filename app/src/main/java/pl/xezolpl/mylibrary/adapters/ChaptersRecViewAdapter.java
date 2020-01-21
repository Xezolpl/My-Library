package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddChapterActivity;
import pl.xezolpl.mylibrary.activities.AddNoteActivity;
import pl.xezolpl.mylibrary.activities.InsertQuoteActivity;
import pl.xezolpl.mylibrary.managers.DeletingManager;
import pl.xezolpl.mylibrary.managers.LinearLayoutManagerWrapper;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class ChaptersRecViewAdapter extends RecyclerView.Adapter<ChaptersRecViewAdapter.ChapterViewHolder> {

    private List<Chapter> chapters = new ArrayList<>();
    private List<ChapterViewHolder> chapterViewHolders = new ArrayList<>();
    private Context context;

    public ChaptersRecViewAdapter(Context context) {
        this.context = context;
    }

    public List<ChapterViewHolder> getChapterViewHolders() {
        return chapterViewHolders;
    }

    public void setChaptersList(List<Chapter> chapters) {
        this.chapters = chapters;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listitem_chapter, parent, false);
        ChapterViewHolder holder = new ChapterViewHolder(v, context);
        chapterViewHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.setData(chapters.get(position));
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public class ChapterViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RecyclerView recView, quotesRecView;
        private RelativeLayout wholeRelLay;
        private Button moreBtn;

        private Chapter thisChapter;

        private NotesRecViewAdapter adapter;
        private QuotesRecViewAdapter quotesAdapter;

        private boolean isRecViewVisible = false;
        private boolean isRecViewVisibleWithChildren = false;

        private Context context;
        private FragmentActivity activity;

        ChapterViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            activity = (FragmentActivity) context;

            initWidgets();
            setOnClickListeners();
            setRecViewVisible(true);

            adapter = new NotesRecViewAdapter(context);
            quotesAdapter = new QuotesRecViewAdapter(context);

            recView.setLayoutManager(new LinearLayoutManagerWrapper(context));
            quotesRecView.setLayoutManager(new LinearLayoutManagerWrapper(context));

            recView.setAdapter(adapter);
            quotesRecView.setAdapter(quotesAdapter);
        }

        private void initWidgets() {
            textView = itemView.findViewById(R.id.textView);
            wholeRelLay = itemView.findViewById(R.id.wholeRelLay);
            moreBtn = itemView.findViewById(R.id.moreBtn);

            recView = itemView.findViewById(R.id.recView);
            recView.setVisibility(View.GONE);

            quotesRecView = itemView.findViewById(R.id.quotes_recView);
            quotesRecView.setVisibility(View.GONE);
        }

        private void setOnClickListeners() {

            wholeRelLay.setOnClickListener(view -> {
                setRecViewVisible(!isRecViewVisible);
                setQuotesViewVisible(!isRecViewVisible);
            });

            wholeRelLay.setOnLongClickListener(view -> {
                expandWithChildren(!isRecViewVisibleWithChildren);
                return true;
            });

            moreBtn.setOnClickListener(view -> {
                //change the  menu
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.chapter_note_popup_menu);

                popupMenu.getMenu().getItem(1).setTitle("Edit chapter");
                popupMenu.getMenu().getItem(3).setTitle("Delete chapter");
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.addMenuBtn: {

                            Intent intent = new Intent(context, AddNoteActivity.class);
                            intent.putExtra("chapter", thisChapter);
                            intent.putExtra("parent", 1);

                            setQuotesViewVisible(true);
                            setRecViewVisible(true);

                            context.startActivity(intent);
                            break;
                        }
                        case R.id.editMenuBtn: {

                            Intent intent;
                            intent = new Intent(context, AddChapterActivity.class);
                            intent.putExtra("chapter", thisChapter);

                            context.startActivity(intent);
                            break;
                        }
                        case R.id.insertQuoteMenuBtn: {

                            Intent intent = new Intent(context, InsertQuoteActivity.class);
                            intent.putExtra("chapter", thisChapter);

                            setRecViewVisible(true);
                            setQuotesViewVisible(true);

                            context.startActivity(intent);
                            break;
                        }
                        case R.id.deleteMenuBtn: {

                            DeletingManager deletingManager = new DeletingManager((AppCompatActivity) context);

                            deletingManager.showDeletingDialog(context.getString(R.string.del_chapter),
                                    context.getString(R.string.delete_chapter),
                                    DeletingManager.CHAPTER,
                                    thisChapter);
                            break;
                        }
                        default:
                            return false;
                    }
                    return true;
                });

                MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popupMenu.getMenu(), view);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
            });

        }

        void setData(Chapter chapter) {

            thisChapter = chapter;
            textView.setText(chapter.getName());

            NoteViewModel noteModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
            noteModel.getNotesByParent(chapter.getId()).observe(activity, notes -> {
                adapter.setNotes(notes);
            });

            QuoteViewModel quoteViewModel = ViewModelProviders.of(activity).get(QuoteViewModel.class);
            quoteViewModel.getQuotesByChapter(chapter.getId()).observe(activity, quotes -> {
                quotesAdapter.setQuotes(quotes);
                quotesRecView.setAdapter(quotesAdapter);
            });

        }

        private void setRecViewVisible(boolean b) {
            recView.setVisibility(b ? View.VISIBLE : View.GONE);
            isRecViewVisible = b;
        }

        private void setQuotesViewVisible(boolean b) {
            quotesRecView.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void expandWithChildren(boolean b) {
            for (NotesRecViewAdapter.NoteViewHolder viewHolder : adapter.getNoteViewHolders()) {
                viewHolder.expandWithChildren(b);
            }
            setRecViewVisible(b);
            isRecViewVisibleWithChildren = b;

        }
    }
}