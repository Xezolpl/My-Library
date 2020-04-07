package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import pl.xezolpl.mylibrary.R;

public class IntroPagerAdapter extends PagerAdapter {
    private Drawable[] images;
    private String[] titles, descriptions ;
    private Context context;

    public IntroPagerAdapter(Context context) {
        this.context = context;
        titles = context.getResources().getStringArray(R.array.intro_titles);
        descriptions = context.getResources().getStringArray(R.array.intro_descriptions);
        images = new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.intro1),
                ContextCompat.getDrawable(context, R.drawable.intro2),
                ContextCompat.getDrawable(context, R.drawable.intro3),
                ContextCompat.getDrawable(context, R.drawable.intro4),
                ContextCompat.getDrawable(context, R.drawable.intro5),
                ContextCompat.getDrawable(context, R.drawable.intro6),
                ContextCompat.getDrawable(context, R.drawable.intro7),
                ContextCompat.getDrawable(context, R.drawable.intro8),
                ContextCompat.getDrawable(context, R.drawable.intro9),
                ContextCompat.getDrawable(context, R.drawable.intro10)
        };
    }


    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.intro_slide_fragment, container, false);

        TextView titleTxtView = view.findViewById(R.id.slide_title);
        TextView descTxtView = view.findViewById(R.id.slide_description);
        ImageView imgView = view.findViewById(R.id.slide_imgView);

        titleTxtView.setText(titles[position]);
        descTxtView.setText(descriptions[position]);
        imgView.setImageDrawable(images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
