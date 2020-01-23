package pl.xezolpl.mylibrary.adapters.Callbacks;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import pl.xezolpl.mylibrary.models.Note;

public class NoteDiffCallback extends DiffUtil.Callback {
    private List<Note> newNotes;
    private List<Note> oldNotes;

    public NoteDiffCallback(List<Note> newNotes, List<Note> oldNotes) {
        this.newNotes = newNotes;
        this.oldNotes = oldNotes;
    }

    @Override
    public int getOldListSize() {
        return oldNotes.size();
    }

    @Override
    public int getNewListSize() {
        return newNotes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotes.get(oldItemPosition).getId().equals(newNotes.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldN = oldNotes.get(oldItemPosition);
        Note newN = newNotes.get(newItemPosition);
        return oldN.getMarkerType() == newN.getMarkerType() &&
                oldN.getColor() == newN.getColor() &&
                 oldN.getNote().equals(newN.getNote());
    }
}
