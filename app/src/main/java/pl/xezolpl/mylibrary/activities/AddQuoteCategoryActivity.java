package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.nikartm.button.FitButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;

import petrov.kristiyan.colorpicker.ColorPicker;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class AddQuoteCategoryActivity extends AppCompatActivity {
    private QuoteCategory thisCategory = null;

    private MaterialEditText name_edtTxt;
    private ImageView selected_color_imgView;
    private FitButton ok_btn, cancel_btn, color_btn;

    private ColorPicker colorPicker;
    private GradientDrawable drawable;

    private int hexdecColor;
    private boolean inEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote_category);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initWidgets();
        setFinishOnTouchOutside(false);
        setOnClickListeners();

        drawable = (GradientDrawable) selected_color_imgView.getBackground();

        if (getIntent().hasExtra("category")) {
            thisCategory = (QuoteCategory) getIntent().getSerializableExtra("category");
            hexdecColor = thisCategory.getColor();
            loadCategoryData();
            inEditing = true;
        } else {
            hexdecColor = Markers.BLUE_START_COLOR;
        }
        drawable.setColor(hexdecColor);

    }

    private void loadCategoryData() {
        name_edtTxt.setText(thisCategory.getName());
        colorPicker.setDefaultColorButton(thisCategory.getColor());
    }

    private void initWidgets() {
        name_edtTxt = findViewById(R.id.add_category_name_edtTxt);
        selected_color_imgView = findViewById(R.id.add_category_selected_color_imgView);
        color_btn = findViewById(R.id.add_category_color_btn);
        ok_btn = findViewById(R.id.add_category_ok_btn);
        cancel_btn = findViewById(R.id.add_category_cancel_btn);
        colorPicker = new ColorPicker(AddQuoteCategoryActivity.this);
    }

    private void setOnClickListeners() {

        color_btn.setOnClickListener(view -> {
            final int hexdecColorCopy = hexdecColor;
            colorPicker.setDefaultColorButton(Markers.BLUE_START_COLOR);
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
        });

        ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                QuoteCategoryViewModel model = ViewModelProviders.of(AddQuoteCategoryActivity.this).get(QuoteCategoryViewModel.class);
                if (inEditing) {
                    model.update(thisCategory);
                } else {
                    model.insert(thisCategory);
                }
                finish();
            }
        });

        cancel_btn.setOnClickListener(view -> finish());
    }

    private boolean areValidOutputs() {

        String id;
        if (thisCategory != null){
            id = thisCategory.getId();
        }else {
            id = UUID.randomUUID().toString();
        }
        String name = name_edtTxt.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Category's name cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        thisCategory = new QuoteCategory(id, name, hexdecColor);
        return true;

    }
}