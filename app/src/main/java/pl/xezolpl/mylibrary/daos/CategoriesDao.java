package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.Categories;

@Dao
public interface CategoriesDao {

    @Insert
    void insert(Categories categories);

    @Update
    void update(Categories categories);

    @Delete
    void delete(Categories categories);

    @Query("SELECT * FROM categories WHERE category=:category")
    LiveData<List<Categories>> getBooksByCategory(String category);
 }
