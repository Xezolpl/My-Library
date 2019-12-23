package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.CategoriesDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.Categories;

public class CategoriesViewModel extends AndroidViewModel {

    private CategoriesDao categoriesDao;
    private LibraryDatabase database;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        categoriesDao = database.CategoriesDao();
    }

    public void insert(Categories categories){
        new InsertAsyncTask(categoriesDao).execute(categories);
    }

    public void update(Categories categories){
        new UpdateAsyncTask(categoriesDao).execute(categories);
    }

    public void delete(Categories categories){
        new DeleteAsyncTask(categoriesDao).execute(categories);
    }

    public LiveData<List<Categories>> getBooksByCategory(String category){
        return categoriesDao.getBooksByCategory(category);
    }

    private class OperationsAsyncTask extends AsyncTask<Categories, Void, Void> {
        protected CategoriesDao operationDao;

        public OperationsAsyncTask(CategoriesDao operationDao) {
            this.operationDao = operationDao;
        }

        @Override
        protected Void doInBackground(Categories... categories) {
            return null;
        }
    }

    private class InsertAsyncTask extends OperationsAsyncTask {

        public InsertAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Categories... categories) {
            operationDao.insert(categories[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Categories... categories) {
            operationDao.update(categories[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(CategoriesDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Categories... categories) {
            operationDao.delete(categories[0]);
            return null;
        }
    }
}
