package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import pl.xezolpl.mylibrary.R;

public class IntroPagerAdapter extends PagerAdapter {
    private int[] images;
    private String[] titles, descriptions ;
    private Context context;

    public IntroPagerAdapter(Context context) {
        this.context = context;
        titles = context.getResources().getStringArray(R.array.intro_titles);
        descriptions = context.getResources().getStringArray(R.array.intro_descriptions);
        //images = context.getResources().getIntArray(R.array.intro_images);
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
        View view = LayoutInflater.from(context).inflate(R.layout.intro_slide, container, false);

        TextView titleTxtView = view.findViewById(R.id.slide_title);
        TextView descTxtView = view.findViewById(R.id.slide_description);
        ImageView imgView = view.findViewById(R.id.slide_imgView);

        titleTxtView.setText(titles[position]);
        descTxtView.setText(descriptions[position]);
        //imgView.setImageDrawable(images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
