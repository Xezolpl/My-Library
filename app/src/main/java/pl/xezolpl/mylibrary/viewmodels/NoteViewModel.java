package pl.xezolpl.mylibrary.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.xezolpl.mylibrary.daos.NoteDao;
import pl.xezolpl.mylibrary.database.LibraryDatabase;
import pl.xezolpl.mylibrary.models.Note;

public class NoteViewModel extends AndroidViewModel {

    private NoteDao noteDao;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        LibraryDatabase database = LibraryDatabase.getDatabase(application);
        noteDao = database.NoteDao();
    }

    public void insert(Note note) {
        new InsertAsyncTask(noteDao).execute(note);
    }

    public void update(Note note) {
        new UpdateAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteAsyncTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> getNotesByParent(String parentId) {
        return noteDao.getNotesByParent(parentId);
    }
    public LiveData<Note> getNote(String id) {
        return noteDao.getNote(id);
    }

    private static class OperationsAsyncTask extends AsyncTask<Note, Void, Void> {
        NoteDao operationDao;

        OperationsAsyncTask(NoteDao operationDao) {
            this.operationDao = operationDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            return null;
        }
    }

    private static class InsertAsyncTask extends OperationsAsyncTask {

        InsertAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            operationDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends OperationsAsyncTask {

        UpdateAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            operationDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends OperationsAsyncTask {

        DeleteAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            operationDao.delete(notes[0]);
            return null;
        }
    }

}
