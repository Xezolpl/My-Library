package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.Book;

@Dao
public interface BookDao {

    @Insert
    void insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY title")
    LiveData<List<Book>> getAllBooks();

    @Query("Select * FROM books WHERE status=:status ORDER BY title")
    LiveData<List<Book>> getBooksWithStatus(int status);

    @Query("SELECT * FROM books WHERE id=:bookId")
    LiveData<Book> getBook(String bookId);


}
