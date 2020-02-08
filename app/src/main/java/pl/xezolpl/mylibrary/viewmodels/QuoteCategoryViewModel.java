package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.QuoteCategoryDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.QuoteCategory;

public class QuoteCategoryViewModel extends AndroidViewModel {
    private QuoteCategoryDao categoryDao;

    public QuoteCategoryViewModel(@NonNull Application application) {
        super(application);
        LibraryDatabase database = LibraryDatabase.getDatabase(application);
        categoryDao = database.QuoteCategoryDao();
    }

    public LiveData<List<QuoteCategory>> getAllCategories() {
        return categoryDao.getAllQuoteCategories();
    }

    public LiveData<QuoteCategory> getCategoryByName(String name) {
        return categoryDao.getQuoteCategory(name);
    }

    public LiveData<QuoteCategory> getCategory(String id) {
        return categoryDao.getCategory(id);
    }

    public void insert(QuoteCategory quote) {
        new InsertAsyncTask(categoryDao).execute(quote);
    }

    public void update(QuoteCategory quote) {
        new UpdateAsyncTask(categoryDao).execute(quote);
    }

    public void delete(QuoteCategory quote) {
        new DeleteAsyncTask(categoryDao).execute(quote);
    }


    //inner classes
    private static class OperationAsyncTask extends AsyncTask<QuoteCategory, Void, Void> {
        QuoteCategoryDao asyncTaskDao;

        OperationAsyncTask(QuoteCategoryDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(QuoteCategory... categories) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationAsyncTask {
        InsertAsyncTask(QuoteCategoryDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(QuoteCategory... categories) {
            asyncTaskDao.insert(categories[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends OperationAsyncTask {
        UpdateAsyncTask(QuoteCategoryDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(QuoteCategory... categories) {
            asyncTaskDao.update(categories[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends OperationAsyncTask {
        DeleteAsyncTask(QuoteCategoryDao dao) {
            super(dao);
        }

        @Override
        protected Void doInBackground(QuoteCategory... categories) {
            asyncTaskDao.delete(categories[0]);
            return null;
        }
    }
}
