package pl.xezolpl.mylibrary.ViewModels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.BookDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.Book;

public class BookViewModel extends AndroidViewModel {

    private BookDao bookDao;
    private LibraryDatabase database;

    public BookViewModel(@NonNull Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        bookDao = database.BookDao();
    }

    public LiveData<List<Book>> getAllBooks() {
        return bookDao.getAllBooks();
    }
    public LiveData<List<Book>> getBookWithStatus(int status) {
        return bookDao.getBooksWithStatus(status);
    }

    public void insert(Book book) {
        new InsertAsyncTask(bookDao).execute(book);
    }

    public void update(Book book) {
        new UpdateAsyncTask(bookDao).execute(book);
    }

    public void delete(Book book) {
        new DeleteAsyncTask(bookDao).execute(book);
    }


    private class OperationsAsyncTask extends AsyncTask<Book,Void,Void> {

        protected BookDao asyncTaskDao;

        public OperationsAsyncTask(BookDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected Void doInBackground(Book... books) {
            return null;
        }
    }

    private class InsertAsyncTask extends OperationsAsyncTask{
        public InsertAsyncTask(BookDao asyncTaskDao) {
            super(asyncTaskDao);
        }

        @Override
        protected Void doInBackground(Book... books) {
            asyncTaskDao.insert(books[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends OperationsAsyncTask{
        public UpdateAsyncTask(BookDao asyncTaskDao) {
            super(asyncTaskDao);
        }

        @Override
        protected Void doInBackground(Book... books) {
            asyncTaskDao.update(books[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends OperationsAsyncTask{
        public DeleteAsyncTask(BookDao asyncTaskDao) {
            super(asyncTaskDao);
        }

        @Override
        protected Void doInBackground(Book... books) {
            asyncTaskDao.delete(books[0]);
            return null;
        }
    }
}
