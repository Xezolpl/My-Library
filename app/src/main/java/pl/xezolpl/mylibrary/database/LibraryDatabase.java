package pl.xezolpl.mylibrary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
        CategoryWithBook.class}, version = 2, exportSchema = false)
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
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return libraryDatabaseInstance;
    }



    // Migration from 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "ALTER TABLE quotes ADD COLUMN favourite INTEGER NOT NULL DEFAULT 0");
        }
    };

    @Override
    public void close() {
        super.close();
        libraryDatabaseInstance = null;
    }
}
