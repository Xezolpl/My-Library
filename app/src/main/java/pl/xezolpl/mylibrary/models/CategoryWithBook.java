package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "categories")
public class CategoryWithBook {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "bookId")
    private String bookId;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    public CategoryWithBook(@NotNull String id, @NonNull String bookId, @NonNull String category) {
        this.id = id;
        this.bookId = bookId;
        this.category = category;
    }

    @NotNull
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
