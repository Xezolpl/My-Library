package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "quotes")
public class Quote implements Serializable {
    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = "quote")
    private String quote;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "page")
    private int page;

    public Quote(String id, String quote, String title, String category, int page) {
        this.id = id;
        this.quote = quote;
        this.title = title;
        this.category = category;
        this.page = page;
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

    public String getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }
}
