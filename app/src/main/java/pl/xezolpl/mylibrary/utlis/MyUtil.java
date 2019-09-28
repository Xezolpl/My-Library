package pl.xezolpl.mylibrary.utlis;

import java.util.ArrayList;

import pl.xezolpl.mylibrary.models.BookModel;
import pl.xezolpl.mylibrary.models.Status;

public class MyUtil {
    private static ArrayList<BookModel> allBooks;
    private static ArrayList<BookModel> wantToReadBooks;
    private static ArrayList<BookModel> currentlyReadingBooks;
    private static ArrayList<BookModel> alreadyReadBooks;

    public MyUtil() {
        if (allBooks == null) {
            allBooks = new ArrayList<>();
            initAllBooks();
        }
        if (wantToReadBooks == null) {
            wantToReadBooks = new ArrayList<>();
            initWantToReadBooks();
        }
        if (currentlyReadingBooks == null) {
            currentlyReadingBooks = new ArrayList<>();
            initCurrentlyReadingBooks();
        }
        if (alreadyReadBooks == null) {
            alreadyReadBooks = new ArrayList<>();
            initAlreadyReadBooks();
        }

    }

    //initiators
    private static void initAllBooks() {
        //TODO: initialize all books from database;
        allBooks.add(new BookModel("xdqd", "Nietzshe",
                "https://www.multibiblioteka.waw.pl/wp-content/uploads/2018/10/ksiazka_na_start-1.jpg",
                "Filozofia egzystencjalna.", 106, 1));
        allBooks.add(new BookModel("Tako rzecze zaratustra", "Nietzshe",
                "https://www.multibiblioteka.waw.pl/wp-content/uploads/2018/10/ksiazka_na_start-1.jpg",
                "Filozofia egzystencjalna.", 1223, 2,Status.CURRENTLY_READING));
        allBooks.add(new BookModel("Antychryst", "Nietzshe",
                "https://www.multibiblioteka.waw.pl/wp-content/uploads/2018/10/ksiazka_na_start-1.jpg",
                "Filozofia egzystencjalna.", 123, 3, Status.WANT_TO_READ));
        allBooks.add(new BookModel("dsadasdasfgasfwwas", "Nietzshe",
                "https://www.multibiblioteka.waw.pl/wp-content/uploads/2018/10/ksiazka_na_start-1.jpg",
                "Filozofia egzystencjalna.", 412, 4, Status.ALREADY_READ));
    }

    private static void initWantToReadBooks() {
        for(BookModel b : allBooks){
            if(b.getStatus()== Status.WANT_TO_READ)
                wantToReadBooks.add(b);
        }
    }

    private static void initCurrentlyReadingBooks() {
        for(BookModel b : allBooks){
            if(b.getStatus()== Status.CURRENTLY_READING)
                currentlyReadingBooks.add(b);
        }
    }

    private static void initAlreadyReadBooks() {
        for(BookModel b : allBooks){
            if(b.getStatus()== Status.ALREADY_READ)
                alreadyReadBooks.add(b);
        }
    }

    //getters
    public static ArrayList<BookModel> getAllBooks() {
        return allBooks;
    }

    public static ArrayList<BookModel> getWantToReadBooks() {
        return wantToReadBooks;
    }

    public static ArrayList<BookModel> getCurrentlyReadingBooks() {
        return currentlyReadingBooks;
    }

    public static ArrayList<BookModel> getAlreadyReadBooks() {
        return alreadyReadBooks;
    }

    //adders
    public boolean addBookToAllBooks(BookModel book) {
        return allBooks.add(book);
    }

    public boolean addWantToReadBook(BookModel book) {
        return wantToReadBooks.add(book);
    }

    public boolean addCurrentlyReadingBook(BookModel book) {
        return currentlyReadingBooks.add(book);
    }

    public boolean addAlreadyReadBook(BookModel book) {
        return alreadyReadBooks.add(book);
    }

    //removers
    public boolean removeBookFromAllBooks(BookModel book) {
        return allBooks.remove(book);
    }

    public boolean removeWantToReadBook(BookModel book) {
        return wantToReadBooks.remove(book);
    }

    public boolean removeCurrentlyReadingBook(BookModel book) {
        return currentlyReadingBooks.remove(book);
    }

    public boolean removeAlreadyReadBook(BookModel book) {
        return alreadyReadBooks.remove(book);
    }
}
