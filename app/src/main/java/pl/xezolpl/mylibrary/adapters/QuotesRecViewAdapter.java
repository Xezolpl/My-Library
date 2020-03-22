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

public class QuotesRecViewAdapter extends RecyclerView.Adapter<QuotesRecViewAdapter.ViewHolder> {
    private Context context;

    ///LISTS OF QUOTES
    private List<Quote> quotes = new ArrayList<>(); // Adapter's operating list
    private List<Quote> quotesFull = new ArrayList<>();  // Full list of quotes because filtering clears the quotes list

    ///INSERTING
    private List<Quote> chapterQuotes; // Used only in inserting the quote to a chapter
    private String chapterId = "123"; // Used for inserting the quote to a chapter

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    ///CATEGORIES
    private List<QuoteCategory> allCategories; // All QuoteCategories used to setting specific category's name to a textView

    //FILTERING
    private String categoryId = "";
    private String filterPattern = "";


    public QuotesRecViewAdapter(Context context) {
        this.context = context;

        QuoteCategoryViewModel viewModel = new ViewModelProvider((FragmentActivity) context).get(QuoteCategoryViewModel.class);
        viewModel.getAllCategories().observe((FragmentActivity) context, quoteCategories -> allCategories = quoteCategories);
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
        this.categoryId = categoryId;
        handleAdvancedFiltering();
    }

    public void setFilterPattern(String filter) {
        this.filterPattern = filter.toLowerCase().trim();
        handleAdvancedFiltering();
    }

    private void handleAdvancedFiltering() {
        List<Quote> operationalList = new ArrayList<>();


        //Filter by filterPattern
        if (!filterPattern.isEmpty()) {
            for (Quote q : quotesFull) {
                if (q.getQuote().toLowerCase().contains(filterPattern) ||
                        q.getTitle().toLowerCase().contains(filterPattern) ||
                        q.getAuthor().toLowerCase().contains(filterPattern)) {
                    operationalList.add(q);
                }
            }
            quotes.clear();
            quotes.addAll(operationalList);
            operationalList.clear();

        } else {
            quotes.clear();
            quotes.addAll(quotesFull);
        }


        //Filter by category
        if (!categoryId.isEmpty()) {
            for (Quote quote : quotes) {
                if (quote.getCategoryId().equals(categoryId)) {
                    operationalList.add(quote);
                }
            }
            quotes.clear();
            quotes.addAll(operationalList);
            operationalList.clear();
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

        if (chapterId.equals(quote.getChapterId())) {
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
            if (!chapterId.equals("123")) {
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
