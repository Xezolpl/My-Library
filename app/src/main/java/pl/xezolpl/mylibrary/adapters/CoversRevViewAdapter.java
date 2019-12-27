package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Cover;

public class CoversRevViewAdapter extends RecyclerView.Adapter<CoversRevViewAdapter.ViewHolder> {

    private Context context;
    private List<Cover> bookCovers = new ArrayList<>();

    public CoversRevViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_cover, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).asBitmap().load(bookCovers.get(position)).into(holder.bookCover);
    }

    @Override
    public int getItemCount() {
        return bookCovers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView bookCover;

        ViewHolder(View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.bookCover);

        }
    }
}
