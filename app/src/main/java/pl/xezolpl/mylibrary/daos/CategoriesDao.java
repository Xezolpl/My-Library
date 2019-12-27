package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.CategoryWithBook;

@Dao
public interface CategoriesDao {

    @Insert
    void insert(CategoryWithBook categoryWithBook);

    @Update
    void update(CategoryWithBook categoryWithBook);

    @Delete
    void delete(CategoryWithBook categoryWithBook);

    @Query("SELECT * FROM categories WHERE category=:category")
    LiveData<List<CategoryWithBook>> getBooksByCategory(String category);

    @Query("SELECT * FROM categories WHERE bookId=:bookId")
    LiveData<List<CategoryWithBook>> getCategoriesByBook(String bookId);
}
