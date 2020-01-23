package pl.xezolpl.mylibrary.adapters.Callbacks;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import pl.xezolpl.mylibrary.models.Quote;

public class QuoteDiffCallback extends DiffUtil.Callback {
    private List<Quote> newQuotes;
    private List<Quote> oldQuotes;

    public QuoteDiffCallback(List<Quote> newQuotes, List<Quote> oldQuotes) {
        this.newQuotes = newQuotes;
        this.oldQuotes = oldQuotes;
    }

    @Override
    public int getOldListSize() {
        return oldQuotes.size();
    }

    @Override
    public int getNewListSize() {
        return newQuotes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldQuotes.get(oldItemPosition).getId().equals(newQuotes.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Quote oldQ = oldQuotes.get(oldItemPosition);
        Quote newQ = newQuotes.get(newItemPosition);

        return  oldQ.getTitle().equals(newQ.getTitle()) &&
                oldQ.getAuthor().equals(newQ.getAuthor()) &&
                oldQ.getQuote().equals(newQ.getQuote()) &&
                oldQ.getCategoryId().equals(newQ.getCategoryId()) &&
                oldQ.getChapterId().equals(newQ.getChapterId()) &&
                oldQ.getPage() == newQ.getPage();
    }
}
