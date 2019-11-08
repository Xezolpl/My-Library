package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import petrov.kristiyan.colorpicker.ColorPicker;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.QuoteCategory;

public class AddCategoryActivity extends AppCompatActivity {
	private static final String TAG = "AddCategoryActivity";

	private QuoteCategory thisCategory = null;

	private EditText name_edtTxt;
	private ImageView selected_color_imgView;
	private Button color_btn, ok_btn, cancel_btn;

	private ColorPicker colorPicker;

	private int hexdecColor;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_category);

		initWidgets();
		setFinishOnTouchOutside(false);
		setOnClickListeners();

		if (getIntent().hasExtra("category")) {
			thisCategory = (QuoteCategory) getIntent().getSerializableExtra("category");
			loadCategoryData();
		} else {
			hexdecColor = 0xF333333;
		}
	}

	private void loadCategoryData() {
		// TODO: Set color_selected color as thisCategory.getColor or make imageView as a dot add_quote_selected_color
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
				colorPicker = new ColorPicker(AddCategoryActivity.this);
				final int hexdecColorCopy = hexdecColor;

				colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
					@Override
					public void onChooseColor(int position, int color) {
						hexdecColor = color;
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
					Intent resultIntent = new Intent();
					resultIntent.putExtra("category", thisCategory);
					setResult(RESULT_OK, resultIntent);
					Toast.makeText(AddCategoryActivity.this, "success color", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});

		cancel_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				Toast.makeText(AddCategoryActivity.this,"Cannot add color",Toast.LENGTH_SHORT).show();
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

		try {
			thisCategory = new QuoteCategory(name, hexdecColor);
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}
		return true;

		//TODO: CHECK THAT HEXDEC COLOR IS REALLY HEXADECIMAL
	}
}