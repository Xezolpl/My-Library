package pl.xezolpl.mylibrary.activities;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import petrov.kristiyan.colorpicker.ColorPicker;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class AddQuoteCategoryActivity extends AppCompatActivity {
	private static final String TAG = "AddQuoteCategoryActivity";

	private QuoteCategory thisCategory = null;

	private EditText name_edtTxt;
	private ImageView selected_color_imgView;
	private Button color_btn, ok_btn, cancel_btn;

	private ColorPicker colorPicker;
	private GradientDrawable drawable;

	private int hexdecColor;
	private boolean inEditing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_quote_category);

		initWidgets();
		setFinishOnTouchOutside(false);
		setOnClickListeners();

		if (getIntent().hasExtra("category")) {
			thisCategory = (QuoteCategory) getIntent().getSerializableExtra("category");
			loadCategoryData();
			inEditing = true;
		} else {
			hexdecColor = 0x000000;
		}

		drawable = (GradientDrawable) selected_color_imgView.getBackground();
		drawable.setColor(hexdecColor);
	}

	private void loadCategoryData() {
		name_edtTxt.setText(thisCategory.getName());
		selected_color_imgView.setColorFilter(thisCategory.getColor());
		colorPicker.setDefaultColorButton(thisCategory.getColor());
	}

	private void initWidgets() {
		name_edtTxt = (EditText) findViewById(R.id.add_category_name_edtTxt);
		selected_color_imgView = (ImageView) findViewById(R.id.add_category_selected_color_imgView);
		color_btn = (Button) findViewById(R.id.add_category_color_btn);
		ok_btn = (Button) findViewById(R.id.add_category_ok_btn);
		cancel_btn = (Button) findViewById(R.id.add_category_cancel_btn);
	}

	private void setOnClickListeners() {

		color_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				colorPicker = new ColorPicker(AddQuoteCategoryActivity.this);
				final int hexdecColorCopy = hexdecColor;

				colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
					@Override
					public void onChooseColor(int position, int color) {
						hexdecColor = color;
						drawable.setColor(color);
					}

					@Override
					public void onCancel() {
						hexdecColor = hexdecColorCopy;
						colorPicker.dismissDialog();
					}
				}).show();
			}
		});

		ok_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (areValidOutputs()) {
					QuoteCategoryViewModel model = ViewModelProviders.of(AddQuoteCategoryActivity.this).get(QuoteCategoryViewModel.class);
					if(inEditing){
						model.update(thisCategory);
					} else{
						model.insert(thisCategory);
					}
					finish();
				}
			}
		});

		cancel_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}

	private boolean areValidOutputs() {

		String name = name_edtTxt.getText().toString();

		if (name.isEmpty()) {
			Toast.makeText(this, "Category's name cannot be empty.", Toast.LENGTH_SHORT).show();
			return false;
		}
		thisCategory = new QuoteCategory(name,hexdecColor);
		return true;

	}
}