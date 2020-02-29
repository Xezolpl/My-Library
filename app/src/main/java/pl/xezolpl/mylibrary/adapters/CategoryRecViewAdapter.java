package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.models.Category;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;

public class CategoryRecViewAdapter extends RecyclerView.Adapter<CategoryRecViewAdapter.ViewHolder> {
    // After click on a category shows books with that category
    public static final int NORMAL_MODE = 1;
    // After click on a category selects it (when adding a new book or editing its categories)
    public static final int SELECT_CATEGORIES_MODE = 2;

    private Context context;
    private int mode;
    private String bookId;

    private List<Category> categories = new ArrayList<>();
    private List<Integer> checkedPositions = new ArrayList<>();

    private CategoriesViewModel categoriesViewModel; // In SELECT_CATEGORIES_MODE

    public CategoryRecViewAdapter(Context context, int mode, String bookId) {
        this.context = context;
        this.mode = mode;
        this.bookId = bookId;

        loadCategories();

        categoriesViewModel = new ViewModelProvider((FragmentActivity) context).get(CategoriesViewModel.class);

        //Load categories for this book
        if (mode == SELECT_CATEGORIES_MODE) {
            categoriesViewModel.getCategoriesByBook(bookId).observe((FragmentActivity) context, categoriesWithBooks -> {
                checkedPositions.clear();
                for (CategoryWithBook categoryWithBook : categoriesWithBooks) {
                    for (int i = 0; i < categories.size(); i++) {
                        if (categoryWithBook.getCategory().equals(context.getString(categories.get(i).getNameR()))) {
                            checkedPositions.add(i);
                            break;
                        }
                    }
                }
                notifyDataSetChanged();
            });
        }
    }

    /**
     * Load all categories and sort them by the name (alphabetically)
     */
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
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Category category = categories.get(position);

        holder.setData(category.getNameR(), category.getImgR());

        holder.relLay.setOnClickListener(view -> { //
            if (mode == NORMAL_MODE) {
                // Begin transition from the categories view to the books in specific category
                ((MainActivity) context).setSelectedCategory(context.getString(category.getNameR()));

            } else if (mode == SELECT_CATEGORIES_MODE) { // Insert or delete CategoryWithBook
                String categoryName = context.getString(category.getNameR());
                if (holder.checked) {
                    categoriesViewModel.delete(new CategoryWithBook(bookId + categoryName, bookId, categoryName));
                    holder.setChecked(false);
                } else {
                    categoriesViewModel.insert(new CategoryWithBook(bookId + categoryName, bookId, categoryName));
                    holder.setChecked(true);
                }
            }
        });

        //Load selected categories
        if (mode == SELECT_CATEGORIES_MODE && checkedPositions.contains(position)) {
            holder.setChecked(true);
        }
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
        private int drawableR;

        ViewHolder(View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);
            relLay = itemView.findViewById(R.id.relLay);
        }

        void setData(int nameR, int imgR) {
            txtView.setText(context.getResources().getString(nameR));
            imgView.setBackground(context.getResources().getDrawable(imgR));
            drawableR = imgR;
        }

        void setChecked(boolean b) {
            checked = b;
            imgView.setBackground(ContextCompat.getDrawable(context, b ? R.drawable.check : drawableR));
        }
    }
}