package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddQuoteActivity;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class QuotesRecViewAdapter extends RecyclerView.Adapter<QuotesRecViewAdapter.ViewHolder> implements Filterable {
    private Context context;
    private LayoutInflater inflater;

    private List<Quote> quotes = new ArrayList<>();
    private List<Quote> quotesFull;
    private List<Quote> chapterQuotes;

    private List<QuoteCategory> allCategories;

    private boolean inserting = false;

    public QuotesRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        QuoteCategoryViewModel viewModel = ViewModelProviders.of((FragmentActivity) context).get(QuoteCategoryViewModel.class);
        viewModel.getAllCategories().observe((FragmentActivity) context, new Observer<List<QuoteCategory>>() {
            @Override
            public void onChanged(List<QuoteCategory> quoteCategories) {
                allCategories = quoteCategories;
            }
        });
    }

    //SETTERS & GETTERS
    public void setInserting(boolean b) {
        inserting = b;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
        quotesFull = new ArrayList<>(quotes);
        chapterQuotes = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void setOnClickListeners(final ViewHolder holder, final Quote q) {

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddQuoteActivity.class);
                intent.putExtra("quote", q);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuoteViewModel quoteViewModel = ViewModelProviders.of((FragmentActivity) context).get(QuoteViewModel.class);
                quoteViewModel.delete(q);
                notifyDataSetChanged();
            }
        });

        holder.quote_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.isExpanded) {
                    holder.setExpanded(true);
                } else {
                    holder.setExpanded(false);
                }
            }
        });

        if (inserting) {
            holder.quote_lay.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!holder.isSelected) {
                        chapterQuotes.add(q);
                        holder.setSelected(true);
                    } else {
                        chapterQuotes.remove(q);
                        holder.setSelected(false);
                    }
                    return false;
                }
            });
        }
    }

    public List<Quote> getChapterQuotes() {
        return chapterQuotes;
    }

    //OVERRIDES
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        //GET CATEGORY
        QuoteCategory category = null;
        final Quote q = quotes.get(position);

        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getName().equals(q.getCategory())) {
                category = allCategories.get(i);
                break;
            }
        }

        //GET COLOR
        int color;
        if (category != null) {
            color = category.getColor();
        }else {
            color = Markers.BLUE_START_COLOR;
        }


        holder.setData(q.getTitle(), q.getQuote(), q.getAuthor(), q.getCategory(), q.getPage(), color);
        setOnClickListeners(holder, q);

    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    //FILTERING
    private Filter quotesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Quote> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(quotesFull);
            } else {
                String filteredPattern = charSequence.toString().toLowerCase().trim();
                for (Quote q : quotesFull) {
                    if (q.getTitle().toLowerCase().contains(filteredPattern) ||
                            q.getQuote().toLowerCase().contains(filteredPattern) ||
                            q.getCategory().toLowerCase().contains(filteredPattern)) {
                        filteredList.add(q);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            quotes.clear();
            quotes.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return quotesFilter;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quote_title_txtView, quote_txtView_expanded, quote_txtView_collapsed,
                category_txtView, quote_page_txtView, quote_author_txtView;
        private ImageView category_imgView;
        private RelativeLayout quote_expanded_lay, quote_collapsed_lay, quote_lay;
        private Button editBtn, delBtn;
        private boolean isExpanded = false;
        private boolean isSelected = false;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            quote_title_txtView = itemView.findViewById(R.id.quote_title_txtView);
            quote_author_txtView = itemView.findViewById(R.id.quote_author_txtView);
            quote_txtView_expanded = itemView.findViewById(R.id.quote_txtView_expanded);
            quote_txtView_collapsed = itemView.findViewById(R.id.quote_txtView_collapsed);
            category_txtView = itemView.findViewById(R.id.category_txtView);
            quote_page_txtView = itemView.findViewById(R.id.quote_page_txtView);
            category_imgView = itemView.findViewById(R.id.category_imgView);

            quote_expanded_lay = itemView.findViewById(R.id.quote_expanded_lay);
            quote_collapsed_lay = itemView.findViewById(R.id.quote_collapsed_lay);
            quote_lay = itemView.findViewById(R.id.quote_lay);

            editBtn = itemView.findViewById(R.id.editBtn);
            delBtn = itemView.findViewById(R.id.delBtn);

            quote_expanded_lay.setVisibility(View.GONE);

        }

        void setData(String title, String quote, String author, String category, int page, int hexdecColor) {
            quote_title_txtView.setText(title);
            quote_author_txtView.setText(author);
            quote_txtView_expanded.setText(quote);
            quote_txtView_collapsed.setText(quote);
            quote_txtView_collapsed.setText(quote);
            category_txtView.setText(category);
            String pageString = "Page: " + page;
            quote_page_txtView.setText(pageString);

            GradientDrawable drawable = (GradientDrawable) category_imgView.getBackground();
            drawable.setColor(hexdecColor);

            if (page == 0) quote_page_txtView.setVisibility(View.GONE); ///TODO:WTF
            else quote_page_txtView.setVisibility(View.VISIBLE);
        }

        void setExpanded(boolean b){
            if(b){
                quote_collapsed_lay.setVisibility(View.GONE);
                quote_expanded_lay.setVisibility(View.VISIBLE);
                isExpanded = true;
            }else{
                quote_expanded_lay.setVisibility(View.GONE);
                quote_collapsed_lay.setVisibility(View.VISIBLE);
                isExpanded = false;
            }
        }

        void setSelected(boolean b) {
            if (b) {
                Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.selected_quote_background, null);
                quote_lay.setBackground(drawable);
                isSelected = true;
            } else {
                Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.quote_background, null);
                quote_lay.setBackground(drawable);
                isSelected = false;
            }
            setExpanded(true);
        }
    }

}
