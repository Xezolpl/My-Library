package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.OpenedBookActivity;
import pl.xezolpl.mylibrary.managers.BackupManager;
import pl.xezolpl.mylibrary.models.Book;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> implements Filterable {
    private Context context;

    private List<Book> books = new ArrayList<>(); // Adapter's operating list
    private List<Book> booksFull; // Full list of books because filtering clears the books list

    private List<Book> filteredBooks = null; // List of filtered books (correlates with
                                             // favouriteBooks because user may want to
                                             // get the favourite books of the filtered list

    private List<Book> favouriteBooks; // List of all favourite books

    private boolean isFavourite = false; // Indicates is the favourite mode enabled


    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }

    /**
     * Sets the adapter's books
     * @param books from the database
     */
    public void setBooks(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);

        booksFull = new ArrayList<>(this.books); // Copy of the list (different reference)
        favouriteBooks = new ArrayList<>();

        for (Book book : books) {
            if (book.isFavourite()) {
                favouriteBooks.add(book); // Fill the list
            }
        }
        notifyDataSetChanged(); // Simply, without callbacks
    }

    /**
     * Sets the favourite mode which shows only that books which are favourite by the user.
     * @param b set on/off the favourite mode
     */
    public void setFavouriteFilter(boolean b) {
        books.clear();
        isFavourite = b;

        if (b) {
            if (filteredBooks != null) { // Is the filtering enabled
                for (Book book : filteredBooks) {
                    if (favouriteBooks.contains(book)) {
                        books.add(book);
                    }
                }
            } else { // Only favourites without specific filters
                books.addAll(favouriteBooks);
            }
        } else { // Set favourite filter off
            books.addAll(filteredBooks != null ? filteredBooks : booksFull);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setData(books.get(position).getTitle(), books.get(position).getImageUrl());

        holder.relLay.setOnClickListener(view -> {
            Intent intent = new Intent(context, OpenedBookActivity.class);
            intent.putExtra("book", books.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public Filter getFilter() {
        return booksFilter;
    }


    private final Filter booksFilter = new Filter() {

        /**
         * Sets the filtering rules, and return the results of filtering
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Book> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) { // The filter is empty
                filteredList.addAll(isFavourite ? favouriteBooks : booksFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();

                for (Book b : booksFull) {
                    // Check for books whose title or author contains the filteredPattern
                    if (b.getTitle().toLowerCase().contains(filteredPattern) ||
                            b.getAuthor().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(b);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        /**
         * Publishes the results of the filtering, distinguish simple filtering from
         * filtering with favourite mode enabled
         * @param charSequence pattern of search
         * @param filterResults results of the performFiltering method - found results
         */
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Book> filteredResults = (List) filterResults.values;
            books.clear();

            if (isFavourite) {
                if (charSequence == null || charSequence.length() == 0) { // If the filter was empty
                    books.addAll(favouriteBooks);
                    filteredBooks = null;
                } else { // If the filter was not empty - check for suitable books
                    for (Book book : filteredResults) {
                        if (favouriteBooks.contains(book)) {
                            books.add(book);
                        }
                    }
                    filteredBooks = new ArrayList<>(books);
                }
            } else { // Filtering without favourite mode
                books.addAll(filteredResults);
                filteredBooks = new ArrayList<>(books);
            }
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookImage;
        private TextView bookTitle;
        private RelativeLayout relLay;

        ViewHolder(View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            relLay = itemView.findViewById(R.id.relLay);
        }

        /**
         * Sets the ViewHolder widgets' data
         * @param title title of the TextView
         * @param imgUrl url to image which will be set in ImageView (by Glide)
         */
        void setData(String title, String imgUrl) {
            bookTitle.setText(title);
            if (new File(imgUrl).exists()) {
                Glide.with(context).asBitmap().load(imgUrl).into(bookImage);
            } else {
                Glide.with(context).asBitmap().load(new BackupManager(context).standardCoverUrl).into(bookImage);
            }
        }
    }
}