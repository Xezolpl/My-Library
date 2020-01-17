package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.fragments.BooksListTabFragment;
import pl.xezolpl.mylibrary.models.Category;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;

public class CategoryRecViewAdapter extends RecyclerView.Adapter<CategoryRecViewAdapter.ViewHolder> {
    public static final int NORMAL_MODE = 1;
    public static final int SELECTING_CATEGORIES_MODE = 2;

    private Context context;
    private int mode;
    private LayoutInflater inflater;
    private List<Category> categories = new ArrayList<>();
    private FragmentManager fm;
    private String bookId;
    private CategoriesViewModel categoriesViewModel;

    private boolean inCategory = false;

    public CategoryRecViewAdapter(Context context, int mode, FragmentManager fm, String bookId) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mode = mode;
        this.fm = fm;
        this.bookId = bookId;
        loadCategories();

        categoriesViewModel = ViewModelProviders.of((FragmentActivity) context).get(CategoriesViewModel.class);
    }

    public void setCategoryPicked(boolean b){
        inCategory = b;
    }

    public boolean isCategoryPicked(){
        return inCategory;
    }

    private void loadCategories() {

        categories.add(new Category(R.string.actionAndAdventure, R.drawable.action));
        categories.add(new Category(R.string.art, R.drawable.art));
        categories.add(new Category(R.string.biography, R.drawable.biography));
        categories.add(new Category(R.string.business, R.drawable.business));
        categories.add(new Category(R.string.comedy, R.drawable.comedy));
        categories.add(new Category(R.string.comic_book, R.drawable.comic_book));
        categories.add(new Category(R.string.cooking, R.drawable.cooking));
        categories.add(new Category(R.string.crime, R.drawable.crime));
        categories.add(new Category(R.string.dictionary, R.drawable.dictionary));
        categories.add(new Category(R.string.drama, R.drawable.drama));
        categories.add(new Category(R.string.fantasy, R.drawable.fantasy));
        categories.add(new Category(R.string.guide, R.drawable.guide));
        categories.add(new Category(R.string.health, R.drawable.health));
        categories.add(new Category(R.string.history, R.drawable.history));
        categories.add(new Category(R.string.horror, R.drawable.horror));
        categories.add(new Category(R.string.journal, R.drawable.journal));
        categories.add(new Category(R.string.mystery, R.drawable.mystery));
        categories.add(new Category(R.string.mythology, R.drawable.mythology));
        categories.add(new Category(R.string.poetry, R.drawable.poetry));
        categories.add(new Category(R.string.politic, R.drawable.politic));
        categories.add(new Category(R.string.psychology, R.drawable.psychology));
        categories.add(new Category(R.string.religion, R.drawable.religion));
        categories.add(new Category(R.string.science, R.drawable.science));
        categories.add(new Category(R.string.science_fiction, R.drawable.science_fiction));
        categories.add(new Category(R.string.self_improvement, R.drawable.self_improvement));
        categories.add(new Category(R.string.textbook, R.drawable.textbook));
        categories.add(new Category(R.string.thriller, R.drawable.thriller));
        categories.add(new Category(R.string.travel, R.drawable.travel));
        categories.add(new Category(R.string.war, R.drawable.war));

        Collections.sort(categories, (category, t1) -> context.getString(category.getNameR()).compareTo(context.getString(t1.getNameR())));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.listitem_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Category category = categories.get(position);

        holder.setData(category.getNameR(), category.getImgR());
        final String categoryName = context.getString(category.getNameR());
        categoriesViewModel.getCategoriesByBook(bookId).observe((FragmentActivity) context, categories -> {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getCategory().equals(categoryName)) {
                    holder.checked = true;
                }
            }
        });


        holder.relLay.setOnClickListener(view -> {
            if (mode == NORMAL_MODE) {
                inCategory = true;
                fm.beginTransaction().replace(R.id.fragment_container,
                        new BooksListTabFragment(context.getString(category.getNameR()))).commit();
                ((MainActivity) context).setNavViewItem(0);

            } else if (mode == SELECTING_CATEGORIES_MODE) {
                String categoryName1 = context.getString(category.getNameR());
                if (holder.checked) {
                    categoriesViewModel.delete(new CategoryWithBook(bookId + categoryName1, bookId, categoryName1));
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                    holder.checked = false;
                } else {
                    categoriesViewModel.insert(new CategoryWithBook(bookId + categoryName1, bookId, categoryName1));
                    Toast.makeText(context, "Category inserted", Toast.LENGTH_SHORT).show();
                    holder.checked = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtView;
        private ImageView imgView;
        private RelativeLayout relLay;
        private boolean checked = false;

        ViewHolder(View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);
            relLay = itemView.findViewById(R.id.relLay);
        }

        void setData(int nameR, int imgR) {
            txtView.setText(context.getResources().getString(nameR));
            imgView.setBackground(context.getResources().getDrawable(imgR));
        }
    }


}