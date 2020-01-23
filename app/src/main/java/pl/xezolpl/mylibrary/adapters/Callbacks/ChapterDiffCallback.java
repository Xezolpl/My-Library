package pl.xezolpl.mylibrary.adapters.Callbacks;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import pl.xezolpl.mylibrary.models.Chapter;

public class ChapterDiffCallback extends DiffUtil.Callback {
    private List<Chapter> newChapters;
    private List<Chapter> oldChapters;

    public ChapterDiffCallback(List<Chapter> newChapters, List<Chapter> oldChapters) {
        this.newChapters = newChapters;
        this.oldChapters = oldChapters;
    }

    @Override
    public int getOldListSize() {
        return oldChapters.size();
    }

    @Override
    public int getNewListSize() {
        return newChapters.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldChapters.get(oldItemPosition).getId().equals(newChapters.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Chapter oldCh = oldChapters.get(oldItemPosition);
        Chapter newCh = newChapters.get(newItemPosition);
        return oldCh.getName().equals(newCh.getName()) &&
                oldCh.getNumber()  ==  newCh.getNumber();
    }
}
