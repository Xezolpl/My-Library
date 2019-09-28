package pl.xezolpl.mylibrary.models;

public class BookModel {
    private String title, author, imageUrl, description;
    private int pages, id;
    private boolean favourite=false;
    private Status status;

    public BookModel(String title, String author, String imageUrl, String description, int pages, int id) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
        this.pages = pages;
        this.id = id;
        this.status= Status.NEUTRAL;
    }
    public BookModel(String title, String author, String imageUrl, String description, int pages, int id, Status status) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.description = description;
        this.pages = pages;
        this.id = id;
        this.status= status;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isFavourite() {
        return favourite;
    }
    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public String toString() {
        return "BookModel{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", pages=" + pages + '\'' +
                ", id=" + id + '\'' +
                ", status=" + status.toString() +
                '}';
    }
}
