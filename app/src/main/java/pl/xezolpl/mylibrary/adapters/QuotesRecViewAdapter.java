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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddQuoteActivity;
import pl.xezolpl.mylibrary.adapters.Callbacks.QuoteDiffCallback;
import pl.xezolpl.mylibrary.managers.DeletingManager;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class QuotesRecViewAdapter extends RecyclerView.Adapter<QuotesRecViewAdapter.ViewHolder> implements Filterable {
    private Context context;
    private LayoutInflater inflater;

    private List<Quote> quotes = new ArrayList<>();
    private List<Quote> quotesFull;
    private List<Quote> chapterQuotes;

    private List<QuoteCategory> allCategories;

    private boolean inserting = false;
    private String chapterId = "";

    public QuotesRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        QuoteCategoryViewModel viewModel = ViewModelProviders.of((FragmentActivity) context).get(QuoteCategoryViewModel.class);
        viewModel.getAllCategories().observe((FragmentActivity) context, quoteCategories ->  allCategories = quoteCategories);
    }

    //SETTERS & GETTERS
    public void setInserting(boolean b) {
        inserting = b;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public void setQuotes(List<Quote> quotes) {
        quotesFull = new ArrayList<>(quotes);
        chapterQuotes = new ArrayList<>();

        QuoteDiffCallback callback = new QuoteDiffCallback(this.quotes, quotes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        this.quotes.clear();
        this.quotes.addAll(quotes);
        diffResult.dispatchUpdatesTo(this);

    }

    public List<Quote> getChapterQuotes() {
        return chapterQuotes;
    }

    //OVERRIDES
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_quote, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //GET CATEGORY
        QuoteCategory category = null;
        final Quote quote = quotes.get(position);

        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getId().equals(quote.getCategoryId())) {
                category = allCategories.get(i);
                break;
            }
        }

        //GET COLOR
        int color;
        if (category != null) {
            color = category.getColor();
        } else {
            color = Markers.BLUE_START_COLOR;
        }

        if (chapterId.equals(quote.getChapterId()) && inserting) {
            holder.setSelected(true);
            chapterQuotes.add(quote);
        }

        holder.setData(quote, color);

       // if (position==0 && !chapterId.equals(quotes.get(0).getChapterId())){
       //     holder.setSelected(false);
       // }
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
                    String thisQuoteCategory = null;
                    for (QuoteCategory category : allCategories) {
                        if (q.getCategoryId().equals(category.getId())) {
                            thisQuoteCategory = category.getName();
                        }
                    }
                    if (q.getTitle().toLowerCase().contains(filteredPattern) || //pattern contains title
                            q.getQuote().toLowerCase().contains(filteredPattern) || //pattern contains quote
                            q.getAuthor().toLowerCase().contains(filteredPattern) || //pattern contains author
                            thisQuoteCategory != null) { // category is not null - pattern contains category
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

        private Quote thisQuote;

        private Context context;

        ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;

            initWidgets();
            setOnClickListeners();

        }

        private void initWidgets() {
            quote_title_txtView = itemView.findViewById(R.id.quote_title_txtView);
            quote_author_txtView = itemView.findViewById(R.id.quote_author_txtView);
            category_txtView = itemView.findViewById(R.id.category_txtView);
            quote_page_txtView = itemView.findViewById(R.id.quote_page_txtView);
            quote_txtView_expanded = itemView.findViewById(R.id.quote_txtView_expanded);
            quote_txtView_collapsed = itemView.findViewById(R.id.quote_txtView_collapsed);

            category_imgView = itemView.findViewById(R.id.category_imgView);

            quote_lay = itemView.findViewById(R.id.quote_lay);
            quote_expanded_lay = itemView.findViewById(R.id.quote_expanded_lay);
            quote_collapsed_lay = itemView.findViewById(R.id.quote_collapsed_lay);

            editBtn = itemView.findViewById(R.id.editBtn);
            delBtn = itemView.findViewById(R.id.delBtn);

            quote_expanded_lay.setVisibility(View.GONE);
        }

        private void setOnClickListeners() {

            editBtn.setOnClickListener(view -> {
                Intent intent = new Intent(context, AddQuoteActivity.class);
                intent.putExtra("quote", thisQuote);
                context.startActivity(intent);
                notifyDataSetChanged();
            });

            delBtn.setOnClickListener(view -> {
                DeletingManager deletingManager = new DeletingManager((AppCompatActivity) context);
                deletingManager.showDeletingDialog(context.getString(R.string.del_quote),
                        context.getString(R.string.delete_quote),
                        DeletingManager.QUOTE,
                        thisQuote);
                notifyDataSetChanged();
            });

            quote_lay.setOnClickListener(view -> {
                if (!isExpanded) {
                    setExpanded(true);
                } else {
                    setExpanded(false);
                }
            });

            if (inserting) {
                quote_lay.setOnLongClickListener(view -> {
                    if (!isSelected) {
                        chapterQuotes.remove(thisQuote);
                        thisQuote.setChapterId(chapterId);
                        chapterQuotes.add(thisQuote);
                        setSelected(true);
                    } else {
                        int index = chapterQuotes.indexOf(thisQuote);
                        if (index > -1)
                        chapterQuotes.get(index).setChapterId("");
                        setSelected(false);
                    }
                    return true;
                });
            }
        }

        void setData(Quote quote, int color) {

            thisQuote = quote;

            //Fill the textviews
            quote_title_txtView.setText(quote.getTitle());
            quote_author_txtView.setText(quote.getAuthor());
            quote_txtView_expanded.setText(quote.getQuote());
            quote_txtView_collapsed.setText(quote.getQuote());
            int page = quote.getPage();
            String pageString = "Page: " + page;
            quote_page_txtView.setText(pageString);

            //Set drawable's color
            GradientDrawable drawable = (GradientDrawable) category_imgView.getBackground();
            drawable.setColor(color);

            //If page is 0 - set its visibility to GONE
            if (page == 0) quote_page_txtView.setVisibility(View.GONE); ///TODO:WTF
            else quote_page_txtView.setVisibility(View.VISIBLE);

            for (int i = 0; i < allCategories.size(); i++) {
                if (allCategories.get(i).getId().equals(quote.getCategoryId())) {
                    category_txtView.setText(allCategories.get(i).getName());
                }
            }
        }

        void setExpanded(boolean b) {
            if (b) {
                quote_collapsed_lay.setVisibility(View.GONE);
                quote_expanded_lay.setVisibility(View.VISIBLE);
                isExpanded = true;
            } else {
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
                quote_lay.setBackground(null);
                isSelected = false;
            }
        }
    }

}
