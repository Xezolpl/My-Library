package pl.xezolpl.mylibrary.utilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.models.Chapter;
import pl.xezolpl.mylibrary.models.Note;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;
import pl.xezolpl.mylibrary.viewmodels.ChapterViewModel;
import pl.xezolpl.mylibrary.viewmodels.NoteViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

/**
 * This class provides deleting items from database with their children.
 */
public class DeletingHelper {

    private BookViewModel bookViewModel;
    private CategoriesViewModel categoriesViewModel;
    private ChapterViewModel chapterViewModel;
    private NoteViewModel noteViewModel;
    private QuoteCategoryViewModel quoteCategoryViewModel;
    private QuoteViewModel quoteViewModel;

    private AppCompatActivity activity = null;
    private Fragment fragment = null;

    /**
     * Setting up view models.
     * @param fragment for classes that inherit from Fragment.
     */
    public DeletingHelper(Fragment fragment) {
        this.fragment = fragment;

        bookViewModel = ViewModelProviders.of(fragment).get(BookViewModel.class);
        categoriesViewModel = ViewModelProviders.of(fragment).get(CategoriesViewModel.class);
        chapterViewModel = ViewModelProviders.of(fragment).get(ChapterViewModel.class);
        noteViewModel = ViewModelProviders.of(fragment).get(NoteViewModel.class);
        quoteCategoryViewModel = ViewModelProviders.of(fragment).get(QuoteCategoryViewModel.class);
        quoteViewModel = ViewModelProviders.of(fragment).get(QuoteViewModel.class);
    }

    /**
     * Setting up view models.
     * @param activity for classes that inherit from AppCompactActivity.
     */
    public DeletingHelper(AppCompatActivity activity) {
        this.activity = activity;

        bookViewModel = ViewModelProviders.of(activity).get(BookViewModel.class);
        categoriesViewModel = ViewModelProviders.of(activity).get(CategoriesViewModel.class);
        chapterViewModel = ViewModelProviders.of(activity).get(ChapterViewModel.class);
        noteViewModel = ViewModelProviders.of(activity).get(NoteViewModel.class);
        quoteCategoryViewModel = ViewModelProviders.of(activity).get(QuoteCategoryViewModel.class);
        quoteViewModel = ViewModelProviders.of(activity).get(QuoteViewModel.class);
    }

    /**
     * Deletes book with its categories, chapters, notes and optionally with quotes.
     * @param book book which will be deleted.
     * @param withQuotes delete also that book's  quotes ?
     */
    public void deleteBook(Book book, boolean withQuotes){
        String bookId = book.getId();

        if(withQuotes){
            quoteViewModel.getQuotesByBook(bookId).observe(fragment == null ? activity : fragment, new Observer<List<Quote>>() {
                @Override
                public void onChanged(List<Quote> quotes) {
                    for(Quote quote : quotes){
                        //deletes every quote
                        quoteViewModel.delete(quote);
                    }
                }
            });
        }


        categoriesViewModel.getCategoriesByBook(bookId).observe(fragment == null ? activity : fragment, new Observer<List<CategoryWithBook>>() {
            @Override
            public void onChanged(List<CategoryWithBook> categoryWithBooks) {
                for (CategoryWithBook category : categoryWithBooks){
                    //deletes book's categories
                    categoriesViewModel.delete(category);
                }
            }
        });


        chapterViewModel.getChaptersByBook(bookId).observe(fragment == null ? activity : fragment, new Observer<List<Chapter>>() {
            @Override
            public void onChanged(List<Chapter> chapters) {
                for (Chapter chapter : chapters){
                    //deletes every chapter and its notes
                    deleteChapter(chapter);
                }
            }
        });

        bookViewModel.delete(book);

    }

    /**
     * Deletes chapter with its notes.
     * @param chapter chapter which will be deleted.
     */
    public void deleteChapter(Chapter chapter){
        String chapterId = chapter.getId();

        noteViewModel.getNotesByParent(chapterId).observe(fragment == null ? activity : fragment, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                for (Note note : notes){
                    deleteNote(note);
                }
            }
        });
        chapterViewModel.delete(chapter);

    }

    /**
     * Deletes note with its children.
     * @param note note which will be deleted.
     */
    public void deleteNote(Note note){
        String noteId = note.getId();

        noteViewModel.getNotesByParent(noteId).observe(fragment == null ? activity : fragment, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                for(Note note1 : notes){
                    deleteNote(note1);
                }
            }
        });
        noteViewModel.delete(note);

    }

    /**
     * Deletes quoteCategory. If quoteCategory isn't "uncategorized" then deletes it.
     * Sets quoteCategory to "uncategorized" everywhere this category was used.
     * @param quoteCategory category which will be deleted
     */
    public void deleteQuoteCategory(QuoteCategory quoteCategory){
        String quoteCategoryId = quoteCategory.getId();
        final String uncategorized = fragment == null ? activity.getString(R.string.uncategorized) : fragment.getString(R.string.uncategorized);

        quoteViewModel.getQuotesByCategory(quoteCategoryId).observe(fragment == null ? activity : fragment, new Observer<List<Quote>>() {
            @Override
            public void onChanged(List<Quote> quotes) {
                for (Quote quote : quotes){
                    //sets category to uncategorized in every quote where this category was used
                    quote.setCategoryId(uncategorized);
                }
            }
        });

        if(!quoteCategoryId.equals(uncategorized)){
            quoteCategoryViewModel.delete(quoteCategory);
        }
    }
}
