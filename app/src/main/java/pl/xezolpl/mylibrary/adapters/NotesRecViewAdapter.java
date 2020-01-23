package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.adapters.Callbacks.NoteDiffCallback;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddNoteActivity;
import pl.xezolpl.mylibrary.managers.DeletingManager;
import pl.xezolpl.mylibrary.managers.LinearLayoutManagerWrapper;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;

import static pl.xezolpl.mylibrary.activities.AddNoteActivity.EDITION;
import static pl.xezolpl.mylibrary.activities.AddNoteActivity.PARENT_NOTE;

public class NotesRecViewAdapter extends RecyclerView.Adapter<NotesRecViewAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private List<NoteViewHolder> noteViewHolders = new ArrayList<>();
    private Context context;

    NotesRecViewAdapter(Context context) {
        this.context = context;
    }

    List<NoteViewHolder> getNoteViewHolders() {
        return noteViewHolders;
    }

    void setNotes(List<Note> notes) {
        final NoteDiffCallback callback = new NoteDiffCallback(this.notes, notes);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        this.notes.clear();
        this.notes.addAll(notes);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listitem_note, parent, false);
        NoteViewHolder viewHolder = new NoteViewHolder(v, context);
        noteViewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        if (!notes.get(position).equals(holder.thisNote))
        holder.setData(notes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull NoteViewHolder holder) {
        if (holder.firstUse){
            holder.setRecViewVisible(false);
            holder.firstUse = false;
        }
        super.onViewAttachedToWindow(holder);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private RecyclerView recView;
        private ImageView marker_imgView;
        private RelativeLayout wholeRelLay;
        private Button moreBtn;

        private NotesRecViewAdapter adapter;

        private boolean isRecViewVisible = false;
        private boolean isRecViewVisibleWithChildren = false;
        private boolean firstUse = true;

        private Context context;
        private FragmentActivity activity;

        private Note thisNote = null;

        NoteViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            activity = (FragmentActivity) context;

            initWidgets();
            setOnClickListeners();
            setRecViewVisible(true);

            adapter = new NotesRecViewAdapter(context);
            recView.setAdapter(adapter);
            recView.setLayoutManager(new LinearLayoutManagerWrapper(context));
        }

        private void initWidgets() {

            textView = itemView.findViewById(R.id.textView);
            recView = itemView.findViewById(R.id.recView);
            wholeRelLay = itemView.findViewById(R.id.wholeRelLay);
            marker_imgView = itemView.findViewById(R.id.marker_imgView);
            moreBtn = itemView.findViewById(R.id.moreBtn);

        }

        private void setOnClickListeners() {

            wholeRelLay.setOnClickListener(view -> setRecViewVisible(!isRecViewVisible));

            wholeRelLay.setOnLongClickListener(view -> {
                expandWithChildren(!isRecViewVisibleWithChildren);
                return true;
            });

            moreBtn.setOnClickListener(view -> {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.note_popup_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.addMenuBtn: {

                            Intent intent = new Intent(context, AddNoteActivity.class);
                            intent.putExtra("note", thisNote);
                            intent.putExtra("parent", PARENT_NOTE);
                            context.startActivity(intent);
                            break;
                        }
                        case R.id.editMenuBtn: {

                            Intent intent;
                            intent = new Intent(context, AddNoteActivity.class);
                            intent.putExtra("note", thisNote);
                            intent.putExtra("parent", EDITION);

                            context.startActivity(intent);
                            break;
                        }
                        case R.id.deleteMenuBtn: {
                            DeletingManager deletingManager = new DeletingManager((AppCompatActivity) context);

                            deletingManager.showDeletingDialog(context.getString(R.string.del_note),
                                    context.getString(R.string.delete_note),
                                    DeletingManager.NOTE,
                                    thisNote);
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

        void setData(Note note, int position) {

            thisNote = note;
            textView.setText(note.getNote());
            try {
                Drawable drawable;
                int markerType = note.getMarkerType();

                if (markerType == Markers.NUMBER_MARKER || markerType == Markers.LETTER_MARKER) {
                    drawable = Markers.getLetterMarker(markerType, position, note.getColor());
                } else {
                    drawable = Markers.getSimpleMarker(context, note.getMarkerType(), note.getColor());
                }
                marker_imgView.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }

            NoteViewModel noteModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
            noteModel.getNotesByParent(note.getId()).observe(activity, notes -> adapter.setNotes(notes));
        }

        private void setRecViewVisible(boolean b) {
            recView.setVisibility(b ? View.VISIBLE : View.GONE);
            isRecViewVisible = b;
            if (!b) isRecViewVisibleWithChildren = false;

        }

        void expandWithChildren(boolean b) {
            for (NoteViewHolder viewHolder : adapter.getNoteViewHolders()) {
                viewHolder.expandWithChildren(b);
            }
            setRecViewVisible(b);
            isRecViewVisibleWithChildren = b;

        }
    }
}