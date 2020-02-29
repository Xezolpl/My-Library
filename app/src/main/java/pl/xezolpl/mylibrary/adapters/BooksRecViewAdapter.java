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
    private List<Book> books = new ArrayList<>();
    private List<Book> favouriteBooks;
    private List<Book> filteredBooks = null;
    private List<Book> booksFull;
    private boolean isFavourite = false;

    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }

    public void setBooks(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);

        booksFull = new ArrayList<>(this.books);
        favouriteBooks = new ArrayList<>();

        for (Book book : books) {
            if (book.isFavourite()) {
                favouriteBooks.add(book);
            }
        }
        notifyDataSetChanged();
    }

    public void setFavouriteFilter(boolean b) {
        books.clear();
        isFavourite = b;
        if (b) {
            if (filteredBooks != null) {
                for (Book book : filteredBooks) {
                    if (favouriteBooks.contains(book)) {
                        books.add(book);
                    }
                }
            } else {
                books.addAll(favouriteBooks);
            }
        } else {
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
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Book> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(isFavourite ? favouriteBooks : booksFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();
                for (Book b : booksFull) {
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

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Book> filteredResults = (List) filterResults.values;
            books.clear();

            if (isFavourite) {
                if (filteredResults.equals(favouriteBooks)) {
                    books.addAll(favouriteBooks);
                    filteredBooks = null;
                } else {
                    for (Book book : filteredResults) {
                        if (favouriteBooks.contains(book)) {
                            books.add(book);
                        }
                    }
                    filteredBooks = new ArrayList<>(books);
                }
            } else {
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