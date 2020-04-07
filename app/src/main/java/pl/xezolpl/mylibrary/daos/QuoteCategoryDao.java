package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.QuoteCategory;

@Dao
public interface QuoteCategoryDao {

    @Insert
    void insert(QuoteCategory category);

    @Update
    void update(QuoteCategory category);

    @Delete
    void delete(QuoteCategory category);

    @Query("SELECT * FROM quoteCategories ORDER BY name")
    LiveData<List<QuoteCategory>> getAllQuoteCategories();

    @Query("SELECT * FROM quoteCategories WHERE id=:id")
    LiveData<QuoteCategory> getCategory(String id);

}
