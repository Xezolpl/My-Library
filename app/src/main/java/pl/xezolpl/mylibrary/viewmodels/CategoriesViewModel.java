package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.CategoriesDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.CategoryWithBook;

public class CategoriesViewModel extends AndroidViewModel {

    private CategoriesDao categoriesDao;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        LibraryDatabase database = LibraryDatabase.getDatabase(application);
        categoriesDao = database.CategoriesDao();
    }

    public void insert(CategoryWithBook categoryWithBook) {
        new InsertAsyncTask(categoriesDao).execute(categoryWithBook);
    }

    public void update(CategoryWithBook categoryWithBook) {
        new UpdateAsyncTask(categoriesDao).execute(categoryWithBook);
    }

    public void delete(CategoryWithBook categoryWithBook) {
        new DeleteAsyncTask(categoriesDao).execute(categoryWithBook);
    }

    public LiveData<List<CategoryWithBook>> getBooksByCategory(String category) {
        return categoriesDao.getBooksByCategory(category);
    }

    public LiveData<List<CategoryWithBook>> getCategoriesByBook(String bookId) {
        return categoriesDao.getCategoriesByBook(bookId);
    }

    private static class OperationsAsyncTask extends AsyncTask<CategoryWithBook, Void, Void> {
        CategoriesDao operationDao;

        OperationsAsyncTask(CategoriesDao operationDao) {
            this.operationDao = operationDao;
        }

        @Override
        protected Void doInBackground(CategoryWithBook... categories) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationsAsyncTask {

        InsertAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(CategoryWithBook... categories) {
            operationDao.insert(categories[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends OperationsAsyncTask {

        UpdateAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(CategoryWithBook... categories) {
            operationDao.update(categories[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends OperationsAsyncTask {

        DeleteAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(CategoryWithBook... categories) {
            operationDao.delete(categories[0]);
            return null;
        }
    }
}
