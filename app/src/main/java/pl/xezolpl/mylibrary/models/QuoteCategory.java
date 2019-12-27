package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "quoteCategories")
public class QuoteCategory implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "color")
    private int color;


    public QuoteCategory(@NonNull String name, int color) {
        this.name = name;
        this.color = color;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
