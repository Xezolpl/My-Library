package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.QuoteDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.Quote;

public class QuoteViewModel extends AndroidViewModel {

    private QuoteDao quoteDao;

    public QuoteViewModel(@NonNull Application application) {
        super(application);
        LibraryDatabase database = LibraryDatabase.getDatabase(application);
        quoteDao = database.QuoteDao();
    }

    public LiveData<List<Quote>> getAllQuotes() {
        return quoteDao.getAllQuotes();
    }

    public LiveData<List<Quote>> getQuotesByCategory(String id) {
        return quoteDao.getQuotesByCategory(id);
    }

    public LiveData<List<Quote>> getQuotesByBook(String bookId) {
        return quoteDao.getQuotesByBook(bookId);
    }

    public LiveData<List<Quote>> getQuotesByChapter(String chapterId) {
        return quoteDao.getQuotesByChapter(chapterId);
    }

    public void insert(Quote quote) {
        new InsertAsyncTask(quoteDao).execute(quote);
    }

    public void update(Quote quote) {
        new UpdateAsyncTask(quoteDao).execute(quote);
    }

    public void delete(Quote quote) {
        new DeleteAsyncTask(quoteDao).execute(quote);
    }


    //inner classes
    private static class OperationAsyncTask extends AsyncTask<Quote, Void, Void> {
        QuoteDao asyncTaskDao;

        OperationAsyncTask(QuoteDao quoteDao) {
            asyncTaskDao = quoteDao;
        }

        @Override
        protected Void doInBackground(Quote... quotes) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationAsyncTask {

        InsertAsyncTask(QuoteDao quoteDao) {
            super(quoteDao);
        }

        @Override
        protected Void doInBackground(Quote... quotes) {
            asyncTaskDao.insert(quotes[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends OperationAsyncTask {
        UpdateAsyncTask(QuoteDao quoteDao) {
            super(quoteDao);
        }

        @Override
        protected Void doInBackground(Quote... quotes) {
            asyncTaskDao.update(quotes[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends OperationAsyncTask {
        DeleteAsyncTask(QuoteDao quoteDao) {
            super(quoteDao);
        }

        @Override
        protected Void doInBackground(Quote... quotes) {
            asyncTaskDao.delete(quotes[0]);
            return null;
        }
    }
}
