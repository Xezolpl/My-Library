package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "chapters")
public class Chapter implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "number")
    private int number;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "page")
    private int page;

    @NonNull
    @ColumnInfo(name = "bookId")
    private String bookId;

    public Chapter(@NonNull String id, int number, @NonNull String name, int page, @NonNull String bookId) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.page = page;
        this.bookId = bookId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getPage() {
        return page;
    }

    @NonNull
    public String getBookId(){
        return bookId;
    }
}
