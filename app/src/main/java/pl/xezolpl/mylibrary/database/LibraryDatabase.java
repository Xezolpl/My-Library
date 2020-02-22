package pl.xezolpl.mylibrary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.xezolpl.mylibrary.daos.BookDao;
import pl.xezolpl.mylibrary.daos.CategoriesDao;
import pl.xezolpl.mylibrary.daos.ChapterDao;
import pl.xezolpl.mylibrary.daos.NoteDao;
import pl.xezolpl.mylibrary.daos.QuoteCategoryDao;
import pl.xezolpl.mylibrary.daos.QuoteDao;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;

@Database(entities = {Book.class, Quote.class, QuoteCategory.class, Chapter.class, Note.class,
        CategoryWithBook.class}, version = 1, exportSchema = false)
public abstract class LibraryDatabase extends RoomDatabase {

    public abstract BookDao BookDao();

    public abstract QuoteDao QuoteDao();

    public abstract QuoteCategoryDao QuoteCategoryDao();

    public abstract ChapterDao ChapterDao();

    public abstract NoteDao NoteDao();

    public abstract CategoriesDao CategoriesDao();

    private static volatile LibraryDatabase libraryDatabaseInstance;

    public static LibraryDatabase getDatabase(final Context context) {
        if (libraryDatabaseInstance == null) {
            synchronized (LibraryDatabase.class) {
                if (libraryDatabaseInstance == null) {
                    libraryDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            LibraryDatabase.class, "library_database.db")
                            .build();
                }
            }
        }
        return libraryDatabaseInstance;
    }

    @Override
    public void close() {
        super.close();
        libraryDatabaseInstance = null;
    }
}
