package pl.xezolpl.mylibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import pl.xezolpl.mylibrary.fragments.BooksListTabFragment;
import pl.xezolpl.mylibrary.models.Book;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> implements Filterable {
    //variables
    private static final String TAG = "BooksRecViewAdapter";
    private Context context;
    private List<Book> books = new ArrayList<>();
    private List<Book> booksFull;
    private LayoutInflater inflater;

    //methods
    public void setBooks(List<Book> books) {
        this.books = books;
        booksFull = new ArrayList<>(this.books);//copy of the list
        notifyDataSetChanged();
    }

    public BooksRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }


    //from adapter overrides
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_book_rec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.setData(books.get(position).getTitle(), books.get(position).getImageUrl());

        holder.relLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OpenedBookActivity.class);
                intent.putExtra("book",books.get(position));

                ((Activity)context).startActivityForResult(intent, BooksListTabFragment.UPDATE_BOOK_ACTIVITY_REQUEST_CODE);
            }
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

    Filter booksFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Book> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(booksFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();
                for (Book b : booksFull) {
                    if (b.getTitle().toLowerCase().contains(filteredPattern)) {
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

    //inner class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookImage;
        private TextView bookTitle;
        private RelativeLayout relLay;

        ViewHolder(View itemView) {
            super(itemView);
            bookImage = (ImageView) itemView.findViewById(R.id.bookImage);
            bookTitle = (TextView) itemView.findViewById(R.id.bookTitle);
            relLay = (RelativeLayout) itemView.findViewById(R.id.relLay);
        }
        void setData(String title, String imgUrl){
            bookTitle.setText(title);
            Glide.with(context).asBitmap().load(imgUrl).into(bookImage);
        }
    }
}
