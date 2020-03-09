package pl.xezolpl.mylibrary.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.xezolpl.mylibrary.models.Chapter;

@Dao
public interface ChapterDao {

    @Insert
    void insert(Chapter chapter);

    @Update
    void update(Chapter chapter);

    @Delete
    void delete(Chapter chapter);

    @Query("SELECT * FROM chapters WHERE bookId=:bookId ORDER BY number")
    LiveData<List<Chapter>> getChaptersByBook(String bookId);

    @Query("SELECT * FROM chapters WHERE id=:id")
    LiveData<Chapter> getChapter(String id);
}
