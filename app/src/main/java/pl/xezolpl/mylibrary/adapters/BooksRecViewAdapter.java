package pl.xezolpl.mylibrary.adapters;

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
import pl.xezolpl.mylibrary.models.BookModel;

public class BooksRecViewAdapter extends RecyclerView.Adapter<BooksRecViewAdapter.ViewHolder> implements Filterable {
    //variables
    private static final String TAG = "BooksRecViewAdapter";
    private Context context;
    private ArrayList<BookModel> books = new ArrayList<>();
    private ArrayList<BookModel> booksFull;

    //methods
    public void setBooks(ArrayList<BookModel> books) {
        this.books = books;
        booksFull = new ArrayList<>(this.books);//copy of the list
        notifyDataSetChanged();
    }

    public BooksRecViewAdapter(Context context) {
        this.context = context;
    }


    //from adapter overrides
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_book_rec_view, parent, false);
        return new ViewHolder(view);// there may be needed to make support
        // variable as ViewHolder holder = new ViewHolder(view) and then return it but I am not sure
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.bookTitle.setText(books.get(position).getTitle());
        holder.relLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OpenedBookActivity.class);
                intent.putExtra("bookId", books.get(position).getId());
                Log.d(TAG, String.valueOf(books.get(position).getId()));
                context.startActivity(intent);
            }
        });
        Glide.with(context).asBitmap().load(books.get(position).getImageUrl()).into(holder.bookImage);
        // photo as url transformed to bitmap into ImageView bookImage

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
            List<BookModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(booksFull);
            }
            else{
                String filteredPatern = charSequence.toString().toLowerCase().trim();
                for (BookModel b : booksFull){
                    if(b.getTitle().toLowerCase().contains(filteredPatern)){
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
        books.addAll((List)filterResults.values);
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
    }

    public ArrayList<BookModel> getBooks() {
        return books;
    }
}
