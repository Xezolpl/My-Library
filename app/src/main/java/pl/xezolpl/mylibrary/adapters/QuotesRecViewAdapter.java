package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Quote;

public class QuotesRecViewAdapter extends RecyclerView.Adapter<QuotesRecViewAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "QuotesRecViewAdapter";
    private int expandedPosition = -1;

    private Context context;
    private LayoutInflater inflater;
    private List<Quote> quotes = new ArrayList<>();
    private List<Quote> quotesFull;

    public QuotesRecViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        Quote q = quotes.get(position);
        holder.setData(q.getTitle(), q.getQuote(), q.getCategory(), q.getPage(), 0xFFFF0000);//TODO: METHOD IN QuoteCategory which takes categoryName and returns QuoteCategory Object
       /* if (position == expandedPosition) {
            holder.quote_lay.setVisibility(View.VISIBLE);
        } else {
            holder.quote_lay.setVisibility(View.GONE);
        }
     */   //TODO: NIE MOZESZ UKRYWAC QUOTE_LAY TYLKO PODZIELIC TO NA DWA OSOBNE LAYOUTY EXPANDED I ZWYKLY I WTEDY TEN EXPANDED DAJESZ DO TEGO IF(VISBLITY)
    }

    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // Check for an expanded view, collapse if you find one
        if (expandedPosition >= 0) {
            int prev = expandedPosition;
            notifyItemChanged(prev);
        }
        // Set the current position to "expanded"
        expandedPosition = holder.getAdapterPosition();
        notifyItemChanged(expandedPosition);

        Toast.makeText(context, "Clicked: "+TAG, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }



    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView quote_title_txtView, quote_txtView, category_txtView, quote_page_txtView;
        private ImageView category_imgView;
        private RelativeLayout quote_lay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quote_title_txtView = (TextView) itemView.findViewById(R.id.quote_title_txtView);
            quote_txtView = (TextView) itemView.findViewById(R.id.quote_txtView);
            category_txtView = (TextView) itemView.findViewById(R.id.category_txtView);
            quote_page_txtView = (TextView) itemView.findViewById(R.id.quote_page_txtView);
            category_imgView = (ImageView) itemView.findViewById(R.id.category_imgView);
            quote_lay = (RelativeLayout) itemView.findViewById(R.id.quote_lay);
        }

        void setData(String title, String quote, String category, int page, int hexdecColor) {
            quote_title_txtView.setText(title);
            quote_txtView.setText(quote);
            category_txtView.setText(category);
            quote_page_txtView.setText("Page: " + page);
            category_imgView.setBackgroundColor(hexdecColor);

            if (page==0) quote_page_txtView.setVisibility(View.INVISIBLE);

        }
    }
}
