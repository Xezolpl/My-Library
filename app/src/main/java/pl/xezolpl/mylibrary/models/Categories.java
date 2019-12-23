package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Categories {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "bookId")
    private String bookId;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    public Categories(String id, @NonNull String bookId, @NonNull String category) {
        this.id = id;
        this.bookId = bookId;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    @NonNull
    public String getBookId() {
        return bookId;
    }

    @NonNull
    public String getCategory() {
        return category;
    }
}
