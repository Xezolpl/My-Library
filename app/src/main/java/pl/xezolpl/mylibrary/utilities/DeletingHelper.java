package pl.xezolpl.mylibrary.utilities;

import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
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

    private AppCompatActivity activity;

    public static final int BOOK = 1;
    public static final int CHAPTER = 2;
    public static final int NOTE = 3;
    public static final int QUOTECATEGORY = 4;
    public static final int QUOTE = 5;

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
            quoteViewModel.getQuotesByBook(bookId).observe(activity, quotes -> {
                for (Quote quote : quotes) {
                    //deletes every quote
                    quoteViewModel.delete(quote);
                }
            });
        }


        categoriesViewModel.getCategoriesByBook(bookId).observe(activity, categoryWithBooks -> {
            for (CategoryWithBook category : categoryWithBooks) {
                //deletes book's categories
                categoriesViewModel.delete(category);
            }
        });


        chapterViewModel.getChaptersByBook(bookId).observe(activity, chapters -> {
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

        noteViewModel.getNotesByParent(chapterId).observe(activity, notes -> {
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

        noteViewModel.getNotesByParent(noteId).observe(activity, notes -> {
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
        final String uncategorized = activity.getString(R.string.uncategorized);

        quoteViewModel.getQuotesByCategory(quoteCategoryId).observe(activity, quotes -> {
            for (Quote quote : quotes) {
                //sets category to uncategorized in every quote where this category was used
                quote.setCategoryId(uncategorized);
            }
        });

        if (!quoteCategoryId.equals(uncategorized)) {
            quoteCategoryViewModel.delete(quoteCategory);
        }
    }

    /**
     * Showing new dialog, with buttons to get user's answer, to delete or not the book.
     *
     * @param title        dialog's title
     * @param message      dialog's message
     * @param type         type of deleting item (Book/Chapter/Note/QuoteCategory/Quote)
     * @param itemToDelete item to delete
     */
    public void showDeletingDialog(String title, String message, int type, Serializable itemToDelete) {
        new EZDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)

                .setPositiveBtnText(activity.getString(R.string.delete))
                .setNeutralBtnText(type == BOOK ? activity.getString(R.string.without_quotes) : null)
                .setNegativeBtnText(activity.getString(R.string.cancel))
                .OnPositiveClicked(() -> {
                    switch (type) {
                        case BOOK: {
                            deleteBook((Book) itemToDelete, true);
                            activity.finish();
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
                .OnNeutralClicked(() -> {
                    deleteBook((Book) itemToDelete, false);
                    activity.finish();
                })
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
