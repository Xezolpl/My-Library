package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class QuotesRecViewAdapter extends RecyclerView.Adapter<QuotesRecViewAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "QuotesRecViewAdapter";
    private int expandedPosition = -1;

    private Context context;
    private LayoutInflater inflater;
    private List<Quote> quotes = new ArrayList<>();
    private List<Quote> quotesFull;
    private List<QuoteCategory> allCategories;

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

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
        quotesFull = new ArrayList<>(this.quotes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: ");

        QuoteCategory category = null;
        Quote q = quotes.get(position);

        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getName().equals(q.getCategory())) {
                category = allCategories.get(i); //TODO TRY TO SIMPLIFY IT AND MAKE IT FASTER!!!!!!!!!!!
                break;
            }
        }

        int color = 0x000000;
        try {
            color = category.getColor();
        } catch (NullPointerException exc) {
            exc.printStackTrace();
        }

        holder.setData(q.getTitle(), q.getQuote(), q.getCategory(), q.getPage(), color);

        holder.quote_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.expanded) {
                    holder.setExpanded();
                } else {
                    holder.setCollapsed();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    @Override
    public Filter getFilter() {
        return quotesFilter;
    }

    Filter quotesFilter = new Filter() {
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

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quote_title_txtView, quote_txtView_expanded, quote_txtView_collapsed, category_txtView, quote_page_txtView;
        private ImageView category_imgView;
        private RelativeLayout quote_expanded_lay, quote_collapsed_lay, quote_lay;
        private boolean expanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quote_title_txtView = (TextView) itemView.findViewById(R.id.quote_title_txtView);
            quote_txtView_expanded = (TextView) itemView.findViewById(R.id.quote_txtView_expanded);
            quote_txtView_collapsed = (TextView) itemView.findViewById(R.id.quote_txtView_collapsed);
            category_txtView = (TextView) itemView.findViewById(R.id.category_txtView);
            quote_page_txtView = (TextView) itemView.findViewById(R.id.quote_page_txtView);
            category_imgView = (ImageView) itemView.findViewById(R.id.category_imgView);

            quote_expanded_lay = (RelativeLayout) itemView.findViewById(R.id.quote_expanded_lay);
            quote_collapsed_lay = (RelativeLayout) itemView.findViewById(R.id.quote_collapsed_lay);
            quote_lay = (RelativeLayout) itemView.findViewById(R.id.quote_lay);

            quote_expanded_lay.setVisibility(View.GONE);
        }

        void setData(String title, String quote, String category, int page, int hexdecColor) {
            quote_title_txtView.setText(title);
            quote_txtView_expanded.setText(quote);
            quote_txtView_collapsed.setText(quote);
            category_txtView.setText(category);
            quote_page_txtView.setText("Page: " + page);

            GradientDrawable drawable = (GradientDrawable) category_imgView.getBackground();
            drawable.setColor(hexdecColor);

            if (page == 0) quote_page_txtView.setVisibility(View.INVISIBLE); ///TODO:WTF
        }

        void setExpanded() {
            quote_collapsed_lay.setVisibility(View.GONE);
            quote_expanded_lay.setVisibility(View.VISIBLE);
            expanded = true;
        }

        void setCollapsed() {
            quote_expanded_lay.setVisibility(View.GONE);
            quote_collapsed_lay.setVisibility(View.VISIBLE);
            expanded = false;
        }
    }

}
