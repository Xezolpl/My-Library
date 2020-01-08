package pl.xezolpl.mylibrary.utilities;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.io.Serializable;

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
import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.Font;

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

    public static final int BOOK = 1;
    public static final int CHAPTER = 2;
    public static final int NOTE = 3;
    public static final int QUOTECATEGORY = 4;
    public static final int QUOTE = 5;


    /**
     * Setting up view models.
     *
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
     *
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
     *
     * @param book       book which will be deleted.
     * @param withQuotes delete also that book's  quotes ?
     */
    private void deleteBook(Book book, boolean withQuotes) {
        String bookId = book.getId();

        if (withQuotes) {
            quoteViewModel.getQuotesByBook(bookId).observe(fragment == null ? activity : fragment, quotes -> {
                for (Quote quote : quotes) {
                    //deletes every quote
                    quoteViewModel.delete(quote);
                }
            });
        }


        categoriesViewModel.getCategoriesByBook(bookId).observe(fragment == null ? activity : fragment, categoryWithBooks -> {
            for (CategoryWithBook category : categoryWithBooks) {
                //deletes book's categories
                categoriesViewModel.delete(category);
            }
        });


        chapterViewModel.getChaptersByBook(bookId).observe(fragment == null ? activity : fragment, chapters -> {
            for (Chapter chapter : chapters) {
                //deletes every chapter and its notes
                deleteChapter(chapter);
            }
        });

        bookViewModel.delete(book);

    }

    /**
     * Deletes chapter with its notes.
     *
     * @param chapter chapter which will be deleted.
     */
    private void deleteChapter(Chapter chapter) {
        String chapterId = chapter.getId();

        noteViewModel.getNotesByParent(chapterId).observe(fragment == null ? activity : fragment, notes -> {
            for (Note note : notes) {
                deleteNote(note);
            }
        });
        chapterViewModel.delete(chapter);

    }

    /**
     * Deletes note with its children.
     *
     * @param note note which will be deleted.
     */
    private void deleteNote(Note note) {
        String noteId = note.getId();

        noteViewModel.getNotesByParent(noteId).observe(fragment == null ? activity : fragment, notes -> {
            for (Note note1 : notes) {
                deleteNote(note1);
            }
        });
        noteViewModel.delete(note);

    }

    /**
     * Deletes quoteCategory. If quoteCategory isn't "uncategorized" then deletes it.
     * Sets quoteCategory to "uncategorized" everywhere this category was used.
     *
     * @param quoteCategory category which will be deleted
     */
    private void deleteQuoteCategory(QuoteCategory quoteCategory) {
        String quoteCategoryId = quoteCategory.getId();
        final String uncategorized = fragment == null ? activity.getString(R.string.uncategorized) : fragment.getString(R.string.uncategorized);

        quoteViewModel.getQuotesByCategory(quoteCategoryId).observe(fragment == null ? activity : fragment, quotes -> {
            for (Quote quote : quotes) {
                //sets category to uncategorized in every quote where this category was used
                quote.setCategoryId(uncategorized);
            }
        });

        if (!quoteCategoryId.equals(uncategorized)) {
            quoteCategoryViewModel.delete(quoteCategory);
        }
    }

    public void showDeletingDialog(String title, String message, int type, Serializable itemToDelete) {

         new EZDialog.Builder(fragment == null ? activity : fragment.getContext())
                .setTitle(title)
                .setMessage(message)

                .setPositiveBtnText(fragment == null ? activity.getString(R.string.delete) : fragment.getString(R.string.delete))
                .setNeutralBtnText(type == BOOK ? (fragment == null ?
                        activity.getString(R.string.without_quotes) :
                        fragment.getString(R.string.without_quotes)) : null)
                .setNegativeBtnText(fragment == null ? activity.getString(R.string.cancel) : fragment.getString(R.string.cancel))

                .OnPositiveClicked(() -> {
                    switch (type) {
                        case BOOK: {
                            deleteBook((Book) itemToDelete, true);
                            break;
                        }
                        case CHAPTER: {
                            deleteChapter((Chapter) itemToDelete);
                            break;
                        }
                        case NOTE: {
                            deleteNote((Note) itemToDelete);
                            break;
                        }
                        case QUOTECATEGORY: {
                            deleteQuoteCategory((QuoteCategory) itemToDelete);
                            break;
                        }
                        case QUOTE: {
                            quoteViewModel.delete((Quote) itemToDelete);
                            break;
                        }
                        default:
                            break;

                    }
                })
                .OnNeutralClicked(() ->
                        deleteBook((Book) itemToDelete, false)
                )
                .OnNegativeClicked(() -> {
                })

                .setTitleDividerLineColor(Color.parseColor("#ed0909"))
                .setTitleTextColor(Color.parseColor("#EE311B"))
                .setButtonTextColor(Color.parseColor("#ed0909"))
                .setMessageTextColor(Color.parseColor("#333333"))
                .setFont(Font.COMFORTAA)

                .setCancelableOnTouchOutside(true)
                .build();

    }
}
