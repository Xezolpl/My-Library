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

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.OpenedBookActivity;
import pl.xezolpl.mylibrary.models.Book;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Book> books = new ArrayList<>();
    private List<Book> booksFull;
    private LayoutInflater inflater;

    public void setBooks(List<Book> books) {
        this.books = books;
        booksFull = new ArrayList<>(this.books);
        notifyDataSetChanged();
    }

    public BooksRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setFavouriteFilter(boolean b){
        if(b){
            books.clear();
            for (Book book : booksFull){
                if (book.isFavourite()) {
                    books.add(book);
                }
            }
        }else {
            books.clear();
            books.addAll(booksFull);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_book, parent, false);
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
                filteredList.addAll(booksFull);
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
            books.clear();
            books.addAll((List) filterResults.values);
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
            if (imgUrl == null) {
                imgUrl = "https://i.pinimg.com/236x/90/49/e5/9049e5a4e33d49807bbbccf25339d266--old-books-vintage-books.jpg";
            }
            Glide.with(context).asBitmap().load(imgUrl).into(bookImage);
        }
    }
}