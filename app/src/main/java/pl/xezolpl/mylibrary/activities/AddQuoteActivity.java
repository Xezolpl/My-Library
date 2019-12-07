package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuoteCategorySpinnerAdapter;
import pl.xezolpl.mylibrary.fragments.QuotesTabFragment;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class AddQuoteActivity extends AppCompatActivity {

    private EditText title_EditTxt, quote_EditTxt, page_EditTxt;
    private Spinner category_spinner;
    private Button add_category_btn, ok_btn, cancel_btn;

    private Quote thisQuote = null;
    private String bookId;

    private QuoteCategoryViewModel categoryViewModel;
    private QuoteCategorySpinnerAdapter spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);

        initWidgets();
        setOnClickListeners();
        setFinishOnTouchOutside(false);

        spinnerAdapter = new QuoteCategorySpinnerAdapter(this);

        categoryViewModel = ViewModelProviders.of(this).get(QuoteCategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(this, new Observer<List<QuoteCategory>>() {
            @Override
            public void onChanged(List<QuoteCategory> quoteCategories) {
                if(quoteCategories.size()==0){
                    categoryViewModel.insert(new QuoteCategory("Uncategorized",0x44BBFF));
                }
                spinnerAdapter.setCategories(quoteCategories);
                category_spinner.setAdapter(spinnerAdapter);
            }
        });

        try {
            bookId = getIntent().getStringExtra("bookId");
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try {
            thisQuote = (Quote) getIntent().getSerializableExtra("quote");
            loadQuoteData(thisQuote);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void initWidgets() {
        title_EditTxt = (EditText) findViewById(R.id.add_quote_title_EditTxt);
        quote_EditTxt = (EditText) findViewById(R.id.add_quote_quote_EditTxt);
        page_EditTxt = (EditText) findViewById(R.id.add_quote_page_EditTxt);
        category_spinner = (Spinner) findViewById(R.id.add_quote_category_spinner);
        add_category_btn = (Button) findViewById(R.id.add_quote_add_category_btn);
        ok_btn = (Button) findViewById(R.id.add_quote_ok_btn);
        cancel_btn = (Button) findViewById(R.id.add_quote_cancel_btn);
    }

    private void loadQuoteData(Quote quote) {
        title_EditTxt.setText(quote.getTitle());
        quote_EditTxt.setText(quote.getQuote());
        page_EditTxt.setText("" + quote.getPage());
        category_spinner.setSelection(spinnerAdapter.getItemPosition(quote.getCategory()));
    }

    private void setOnClickListeners() {

        add_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddQuoteActivity.this, AddQuoteCategoryActivity.class);
                startActivityForResult(intent, QuotesTabFragment.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("quote", thisQuote);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private boolean areValidOutputs() {
        String title = title_EditTxt.getText().toString();
        String quote = quote_EditTxt.getText().toString();
        int page = 0;
        String category = ((QuoteCategory) spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())).getName();
        //TODO: HARD-CODED "UNCATEGORIZED" QUOTE CATEGORY

        String id;

        if (page_EditTxt.length() > 0) {
            try {
                page = Integer.valueOf(page_EditTxt.getText().toString());
            } catch (NumberFormatException exc) {
                Toast.makeText(this, "Type pages as a number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (quote.length() < 3) {
            Toast.makeText(this, "Quote can't be that short!", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            id = thisQuote.getId();
        } catch (NullPointerException exc) {
            id = UUID.randomUUID().toString();
        }

        thisQuote = new Quote(id, quote, title, category, page, bookId);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == QuotesTabFragment.ADD_CATEGORY_ACTIVITY_REQUEST_CODE) {
                QuoteCategory category = (QuoteCategory) data.getSerializableExtra("category");
                categoryViewModel.insert(category);
            } else if (requestCode == QuotesTabFragment.EDIT_CATEGORY_ACTIVITY_REQUEST_CODE) {

            }
        }
    }
}
