package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Category;

public class CategoryRecViewAdapter extends RecyclerView.Adapter<CategoryRecViewAdapter.ViewHolder>{

	private Context context;
	private LayoutInflater inflater;
	private List<Category> categories = new ArrayList<>();
	
	public CategoryRecViewAdapter(Context context){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		loadCategories();
	}

	private void loadCategories(){

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

		Collections.sort(categories, new Comparator<Category>() {
			@Override
			public int compare(Category category, Category t1) {
				return context.getString(category.getNameR()).compareTo(context.getString(t1.getNameR()));
			}
		});
	}

	@NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.listitem_category, parent, false);
		return new ViewHolder(view);
	}

	@Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
		Category category = categories.get(position);

		holder.setData(category.getNameR(), category.getImgR());
		
		holder.relLay.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				//TODO: set view of books with this category!!!
			}
		});
	}

	@Override
    public int getItemCount() {
		return categories.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder {
		private TextView txtView;
		private ImageView imgView;
		private RelativeLayout relLay;
		
		public ViewHolder(View itemView){
			super(itemView);
			
			txtView = itemView.findViewById(R.id.txtView);
			imgView = itemView.findViewById(R.id.imgView);
			relLay = itemView.findViewById(R.id.relLay);
		}
		
		public void setData(int nameR, int imgR){
			txtView.setText(context.getResources().getString(nameR));
			imgView.setBackground(context.getResources().getDrawable(imgR));
		}
	}
}