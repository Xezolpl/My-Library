package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "books")
public class Book implements Serializable {

    @NonNull
    @PrimaryKey
    private String id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "pages")
    private int pages;

    @ColumnInfo(name = "favourite")
    private boolean favourite;

    @ColumnInfo(name = "status")
    private int status;


    public static final int STATUS_NEUTRAL = 0;
    public static final int STATUS_WANT_TO_READ = 1;
    public static final int STATUS_CURRENTLY_READING = 2;
    public static final int STATUS_ALREADY_READ = 3;


    public Book(String title, String author, String imageUrl, String description, int pages, String id, int status) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
        this.pages = pages;
        this.id = id;
        this.status = status;
        favourite = false;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPages() {
        return pages;
    }

    public String getId() {
        return id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public int getStatus() {
        return status;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", pages=" + pages + '\'' +
                ", id=" + id + '\'' +
                ", status=" + status +
                '}';
    }
}
