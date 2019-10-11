package pl.xezolpl.mylibrary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.xezolpl.mylibrary.daos.BookDao;
import pl.xezolpl.mylibrary.models.Book;

@Database(entities = Book.class, version = 1, exportSchema = false)
public abstract class LibraryDatabase extends RoomDatabase{

    public abstract BookDao BookDao();
    private static volatile LibraryDatabase libraryDatabaseInstance;

    public static LibraryDatabase getDatabase(final Context context){
        if(libraryDatabaseInstance ==null){
            synchronized (LibraryDatabase.class){
                if (libraryDatabaseInstance ==null){
                    libraryDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            LibraryDatabase.class,"library_database").build();
                }
            }
        }
        return libraryDatabaseInstance;
    }

}
