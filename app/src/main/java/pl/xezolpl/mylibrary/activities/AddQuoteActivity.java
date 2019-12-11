package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.graphics.Color;
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
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class AddQuoteActivity extends AppCompatActivity {

    private EditText title_EditTxt, quote_EditTxt, page_EditTxt;
    private Spinner category_spinner;
    private Button add_category_btn, ok_btn, cancel_btn;

    private Quote thisQuote = null;
    private boolean inEdition = false;
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
        loadFromIntent();

        spinnerAdapter = new QuoteCategorySpinnerAdapter(this);
        categoryViewModel = ViewModelProviders.of(this).get(QuoteCategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(this, new Observer<List<QuoteCategory>>() {
            @Override
            public void onChanged(List<QuoteCategory> quoteCategories) {
                if (quoteCategories.size() == 0) {
                    QuoteCategory qc = new QuoteCategory("Uncategorized", Color.parseColor("#44BBFF"));
                    categoryViewModel.insert(qc);
                    quoteCategories.add(qc);
                }
                spinnerAdapter.setCategories(quoteCategories);
                category_spinner.setAdapter(spinnerAdapter);

                if(inEdition) loadQuoteData(thisQuote);
            }
        });
    }

    private void loadFromIntent(){
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

       if(intent.hasExtra("quote")){
            thisQuote = (Quote) getIntent().getSerializableExtra("quote");
            bookId = thisQuote.getBookId();
            inEdition = true;
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
                startActivityForResult(intent, QuotesTabFragment.ADD_CATEGORY_ACTIVITY_REQUEST_CODE);//todo here
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {

                    QuoteViewModel viewModel = ViewModelProviders.of(AddQuoteActivity.this).get(QuoteViewModel.class);
                    if (inEdition) {
                        viewModel.update(thisQuote);
                    } else {
                        viewModel.insert(thisQuote);
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

        String title, quote, id, category;
        int page = 0;

        //isQuoteShorterThan3
        if (quote_EditTxt.getText().length() < 3) {
            Toast.makeText(this, "Quote can't be that short!", Toast.LENGTH_SHORT).show();
            return false;
        }

        //gettingId
        try {
            id = thisQuote.getId();
        } catch (NullPointerException exc) {
            id = UUID.randomUUID().toString();
        }

        //getting other strings
        try {
            title = title_EditTxt.getText().toString();
            quote = quote_EditTxt.getText().toString();
            category = ((QuoteCategory) spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())).getName();
        }catch (Exception exc){
            exc.printStackTrace();
            return false;
        }

        //isPageANumber -> getPage
        if (page_EditTxt.length() > 0) {
            try {
                page = Integer.valueOf(page_EditTxt.getText().toString());
            } catch (NumberFormatException exc) {
                Toast.makeText(this, "Type pages as a number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        thisQuote = new Quote(id, quote, title, category, page, bookId);
        return true;
    }
}
