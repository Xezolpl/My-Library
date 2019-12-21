package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.ChapterDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.Chapter;

public class ChapterViewModel extends AndroidViewModel {

    private ChapterDao chapterDao;
    private LibraryDatabase database;

    public ChapterViewModel(@NonNull Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        chapterDao = database.ChapterDao();
    }

    public void insert(Chapter chapter){
        new InsertAsyncTask(chapterDao).execute(chapter);
    }

    public void update(Chapter chapter){
        new UpdateAsyncTask(chapterDao).execute(chapter);
    }

    public void delete(Chapter chapter){
        new DeleteAsyncTask(chapterDao).execute(chapter);
    }

    public LiveData<List<Chapter>> getAllChapters() {
        return chapterDao.getAllChapters();
    }

    public LiveData<List<Chapter>> getChaptersByBook(String bookId) {
        return chapterDao.getChaptersByBook(bookId);
    }

    public LiveData<Chapter> getChapter(String id) {
        return chapterDao.getChapter(id);
    }

    private class OperationsAsyncTask extends AsyncTask<Chapter, Void, Void> {
        protected ChapterDao operationDao;

        public OperationsAsyncTask(ChapterDao operationDao) {
            this.operationDao = operationDao;
        }

        @Override
        protected Void doInBackground(Chapter... chapters) {
            return null;
        }
    }

    private class InsertAsyncTask extends OperationsAsyncTask {

        public InsertAsyncTask(ChapterDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Chapter... chapters) {
            operationDao.insert(chapters[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(ChapterDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Chapter... chapters) {
            operationDao.update(chapters[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(ChapterDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Chapter... chapters) {
            operationDao.delete(chapters[0]);
            return null;
        }
    }

}
