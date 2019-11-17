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
    private LibraryDatabase database;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        database = LibraryDatabase.getDatabase(application);
        noteDao = database.noteDao();
    }

    public void insert(Note note){
        new InsertAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteAsyncTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public LiveData<List<Note>> getNotesByParent(String parentId) {
        return noteDao.getNotesByParent(parentId);
    }

    public LiveData<Note> getNote(String id) {
        return noteDao.getNote(id);
    }

    private class OperationsAsyncTask extends AsyncTask<Note, Void, Void> {
        protected NoteDao operationDao;

        public OperationsAsyncTask(NoteDao operationDao) {
            this.operationDao = operationDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            return null;
        }
    }

    private class InsertAsyncTask extends OperationsAsyncTask {

        public InsertAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private class UpdateAsyncTask extends OperationsAsyncTask {

        public UpdateAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private class DeleteAsyncTask extends OperationsAsyncTask {

        public DeleteAsyncTask(NoteDao operationDao) {
            super(operationDao);
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

}
