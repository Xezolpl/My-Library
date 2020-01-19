package pl.xezolpl.mylibrary.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "markerType")
    private int markerType;

    @NonNull
    @ColumnInfo(name = "note")
    private String note;

    @NonNull
    @ColumnInfo(name = "parentId")
    private String parentId;

    @ColumnInfo(name = "color")
    private int color;


    public Note(@NonNull String id, int markerType, @NonNull String note, @NonNull String parentId, int color) {
        this.id = id;
        this.markerType = markerType;
        this.note = note;
        this.parentId = parentId;
        this.color = color;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getMarkerType() {
        return markerType;
    }

    @NonNull
    public String getNote() {
        return note;
    }

    @NonNull
    public String getParentId() {
        return parentId;
    }

    public int getColor() {
        return color;
    }

}
