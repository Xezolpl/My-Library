package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.Quote;

@Dao
public interface QuoteDao {

    @Insert
    void insert(Quote quote);

    @Update
    void update(Quote quote);

    @Delete
    void delete(Quote quote);

    @Query("SELECT * FROM quotes ORDER BY quote")
    LiveData<List<Quote>> getAllQuotes();

    @Query("SELECT * FROM quotes WHERE id=:quoteId")
    LiveData<Quote> getQuote(String quoteId);

    @Query("SELECT * FROM quotes WHERE categoryId=:category ORDER BY page")
    LiveData<List<Quote>> getQuotesByCategory(String category);

    @Query("SELECT * FROM quotes WHERE bookId=:bookId ORDER BY page")
    LiveData<List<Quote>> getQuotesByBook(String bookId);

    @Query("SELECT * FROM quotes WHERE chapterId=:chapterId ORDER BY page")
    LiveData<List<Quote>> getQuotesByChapter(String chapterId);
}