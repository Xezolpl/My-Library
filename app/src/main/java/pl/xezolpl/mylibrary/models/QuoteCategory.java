package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quoteCategories")
public class QuoteCategory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "color")
    private int hexadecimalColor;


    public QuoteCategory(@NonNull String name, @NonNull int hexadecimalColor) {
        this.name = name;
        this.hexadecimalColor = hexadecimalColor;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public int getColor(){
        return hexadecimalColor;
    }
}
