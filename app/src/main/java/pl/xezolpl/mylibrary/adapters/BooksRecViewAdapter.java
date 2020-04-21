package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.OpenedBookActivity;
import pl.xezolpl.mylibrary.managers.BackupManager;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> {
    private Context context;

    private List<Book> books = new ArrayList<>();
    private List<Book> booksFull = new ArrayList<>();

    private int statusMode = 0;
    private String filterPattern = "";
    private boolean isFavouriteEnabled = false;
    private String categoryId = "";


    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }

    /**
     * Sets the adapter's books
     *
     * @param books from the database
     */
    public void setBooks(List<Book> books) {
        this.books.clear();
        this.books.addAll(books);
        booksFull = new ArrayList<>(books);

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


    public void setStatusMode(int status){
        statusMode = status;
        handleAdvancedFiltering();
    }

    public void setFilterPattern(String filter){
        filterPattern = filter.toLowerCase().trim();
        handleAdvancedFiltering();
    }

    public void setFavouriteEnabled(boolean b){
        isFavouriteEnabled = b;
        handleAdvancedFiltering();
    }

    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
        handleAdvancedFiltering();
    }

    private void handleAdvancedFiltering() {
        books.clear();
        List<Book> operationalList = new ArrayList<>();

        //Handle status
        if (statusMode == 0) books.addAll(booksFull);
        else {
            for (Book b : booksFull) {
                if (b.getStatus() == statusMode) {
                    books.add(b);
                }
            }
        }

        //Handle favourite
        if (isFavouriteEnabled) {
            for (Book b : books) {
                if (b.isFavourite()) {
                    operationalList.add(b);
                }
            }
            books.clear();
            books.addAll(operationalList);
            operationalList.clear();
        }

        //Handle filter
        if (!filterPattern.isEmpty()) {
            for (Book b : books) {
                if (b.getTitle().toLowerCase().contains(filterPattern) ||
                        b.getAuthor().toLowerCase().contains(filterPattern)) {
                    operationalList.add(b);
                }
            }
            books.clear();
            books.addAll(operationalList);
            operationalList.clear();
        }

        if (!categoryId.isEmpty()) {
            CategoriesViewModel cvm = new ViewModelProvider((FragmentActivity) context).get(CategoriesViewModel.class);
            cvm.getBooksByCategory(categoryId).observe((FragmentActivity) context, categories -> {

                for (int i = 0; i < categories.size(); i++) { // Iterating on the List<CategoryWithBook>
                    for (int j = 0; j < books.size(); j++) { // Iterating on the List<Book>
                        Book book = books.get(j);
                        if (categories.get(i).getBookId().equals(book.getId())) {
                            // If category's bookId is equal to book's id then add it to the list
                            operationalList.add(book);
                            break;
                        }
                    }
                }
                books.clear();
                books.addAll(operationalList);
                operationalList.clear();
                notifyDataSetChanged();
            });
        }
        notifyDataSetChanged();
    }

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
         *
         * @param title  title of the TextView
         * @param imgUrl url to image which will be set in ImageView (by Glide)
         */
        void setData(String title, String imgUrl) {
            bookTitle.setText(title);
            if (new File(imgUrl).exists() || imgUrl.contains("books.google")) {
                Glide.with(context).asBitmap().load(imgUrl).into(bookImage);
            } else {
                Glide.with(context).asBitmap().load(new BackupManager(context).standardCoverUrl).into(bookImage);
            }
        }
    }
}