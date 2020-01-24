package pl.xezolpl.mylibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class CoversRevViewAdapter extends RecyclerView.Adapter<CoversRevViewAdapter.ViewHolder> {

    private Context context;
    private List<String> bookCovers = new ArrayList<>();
    private String selectedCover;

    public CoversRevViewAdapter(Context context) {
        this.context = context;
    }

    public void setBookCovers(List<String> bookCovers) {
        this.bookCovers = bookCovers;
        notifyDataSetChanged();
    }

    public String getSelectedCover() {
        return selectedCover;
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
        String thisCover = bookCovers.get(position);
        Glide.with(context).asBitmap().load(thisCover).into(holder.bookCover);
        holder.bookCover.setOnClickListener(view -> {
            selectedCover = thisCover;
            Activity act = ((Activity)context);
            act.setResult(Activity.RESULT_OK, new Intent().putExtra("url", selectedCover));
            act.finish();
        });
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
