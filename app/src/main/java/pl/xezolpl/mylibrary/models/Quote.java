package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "quotes")
public class Quote implements Serializable {

    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = "quote")
    private String quote;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "page")
    private int page;

    @ColumnInfo(name = "bookId")
    private String bookId;

    @ColumnInfo(name = "chapterId")
    private String chapterId;

    public Quote(@NonNull String id, @NonNull String quote, String title, String author, String category, int page, String bookId) {
        this.id = id;
        this.quote = quote;
        this.title = title;
        this.author = author;
        this.category = category;
        this.page = page;
        this.bookId = bookId;
        this.chapterId = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getQuote() {
        return quote;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    public String getBookId() {
        return bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
}
