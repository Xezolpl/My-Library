package pl.xezolpl.mylibrary.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.QuoteCategory;

public class QuoteCategorySpinnerAdapter extends BaseAdapter {

    private int[] colours;
    private String[] names;
    private List<QuoteCategory> categories;
    private LayoutInflater inflater;

    public QuoteCategorySpinnerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setCategories(List<QuoteCategory> quoteCategories) {
        categories = quoteCategories;
        names = new String[categories.size()];
        colours = new int[categories.size()];

        for (int i = 0; i < categories.size(); i++) {
            names[i] = categories.get(i).getName();
            colours[i] = categories.get(i).getColor();
        }
    }

    public int getItemPosition(String s) {
        for (int i = 0; i < names.length; i++) {
            if (s.equals(names[i])) return i;
        }
        return 0;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listitem_quote_category_spinner, viewGroup, false);
        ImageView imgView = view.findViewById(R.id.spinner_category_imgView);
        TextView txtView = view.findViewById(R.id.spinner_category_txtView);

        txtView.setText(names[i]);
        GradientDrawable drawable = (GradientDrawable) imgView.getBackground();
        drawable.setColor(colours[i]);

        return view;
    }
}
