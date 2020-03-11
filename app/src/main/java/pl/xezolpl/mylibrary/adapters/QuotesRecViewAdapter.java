package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
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

    private List<Quote> quotes = new ArrayList<>(); // Adapter's operating list

    private List<Quote> quotesFull;  // Full list of quotes because filtering clears the quotes list

    private List<Quote> chapterQuotes; // Used only in inserting the quote to a chapter

    private List<Quote> categoryQuotes = new ArrayList<>(); // All quotes with category specified by setCategoryFilter()

    private List<Quote> filteredList = new ArrayList<>(); // All quotes received by filtering

    private List<QuoteCategory> allCategories; // All QuoteCategories used to setting specific category's name to a textView

    private boolean isCategoryFilterEnabled = false; // Flag for filtering with a category
    private boolean isFiltered = false;
    private String filteredPattern = "";

    private boolean inserting = false; // Normal mode or inserting mode

    private String chapterId = ""; // Used for inserting the quote to a chapter

    public QuotesRecViewAdapter(Context context) {
        this.context = context;

        QuoteCategoryViewModel viewModel = new ViewModelProvider((FragmentActivity) context).get(QuoteCategoryViewModel.class);
        viewModel.getAllCategories().observe((FragmentActivity) context, quoteCategories -> allCategories = quoteCategories);
    }

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

    public void setCategoryFilter(String categoryId) {
        isCategoryFilterEnabled = true;
        categoryQuotes.clear();

        if (categoryId.isEmpty()) {
            quotes.clear();
            quotes.addAll(isFiltered ? filteredList : quotesFull);
            categoryQuotes.addAll(quotesFull);
        } else {

            for (Quote quote : quotesFull) {
                if (quote.getCategoryId().equals(categoryId)) {
                    categoryQuotes.add(quote);
                }
            }

            if (!filteredPattern.isEmpty()){
                getFilter().filter(filteredPattern);
            }
            quotes.clear();
            quotes.addAll(categoryQuotes);
        }
        notifyDataSetChanged();
    }

    /**
     * Used only for inserting quotes to the chapter
     *
     * @return list of chapters selected in InsertQuoteActivity
     */
    public List<Quote> getChapterQuotes() {
        return chapterQuotes;
    }

    //OVERRIDES
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_quote, parent, false);
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

        if (inserting && chapterId.equals(quote.getChapterId())) {
            holder.setSelected(true);
            chapterQuotes.add(quote);
        }

        holder.setData(quote, color);
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
            filteredList.clear();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(isCategoryFilterEnabled ? categoryQuotes : quotesFull);
                isFiltered = false;
                filteredPattern = "";
            } else {
                filteredPattern = charSequence.toString().toLowerCase().trim();

                for (Quote q : quotesFull) {
                    if (q.getTitle().toLowerCase().contains(filteredPattern) || //pattern contains title
                            q.getQuote().toLowerCase().contains(filteredPattern) || //pattern contains quote
                            q.getAuthor().toLowerCase().contains(filteredPattern)) //pattern contains author
                    {
                        if (isCategoryFilterEnabled) {
                            if (categoryQuotes.contains(q)) {
                                filteredList.add(q);
                            }
                        } else {
                            filteredList.add(q);
                        }

                    }
                }
                isFiltered = true;
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<Quote> filteredQuotes = (List) filterResults.values;

            QuoteDiffCallback callback = new QuoteDiffCallback(quotes, filteredQuotes);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

            quotes.clear();
            quotes.addAll(filteredQuotes);

            diffResult.dispatchUpdatesTo(QuotesRecViewAdapter.this);

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

        private int selectedColor, backgroundColor;

        ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;

            initWidgets();
            setOnClickListeners();

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();

            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            selectedColor = typedValue.data;

            theme.resolveAttribute(R.attr.colorBackground, typedValue, true);
            backgroundColor = typedValue.data;
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

            // If in inserting - long click is a selection of quotes which we want to insert to the chapter
            if (inserting) {
                quote_lay.setOnLongClickListener(view -> {
                    if (!isSelected) {
                        chapterQuotes.remove(thisQuote);
                        thisQuote.setChapterId(chapterId);
                        chapterQuotes.add(thisQuote);
                        setSelected(true);
                    } else {
                        int index = chapterQuotes.indexOf(thisQuote);
                        if (index >= 0)
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
            String pageString = context.getString(R.string.page) + " " + page;
            quote_page_txtView.setText(pageString);

            //Set drawable's color
            GradientDrawable drawable = (GradientDrawable) category_imgView.getBackground();
            drawable.setColor(color);

            //If page is 0 -> set its visibility to INVISIBLE
            if (page == 0) quote_page_txtView.setVisibility(View.INVISIBLE);
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

        //Only visual stylization
        void setSelected(boolean b) {
            if (b) {
                quote_lay.setBackgroundColor(selectedColor);
                isSelected = true;
            } else {
                quote_lay.setBackgroundColor(backgroundColor);
                isSelected = false;
            }
        }
    }

}
