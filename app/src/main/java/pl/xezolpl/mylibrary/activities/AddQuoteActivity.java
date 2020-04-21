package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.nikartm.button.FitButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuoteCategorySpinnerAdapter;
import pl.xezolpl.mylibrary.managers.DeletingManager;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.PermissionsManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.utilities.Requests;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class AddQuoteActivity extends AppCompatActivity {
    private static final String TAG = "AddQuoteActivity";

    private EditText title_EditTxt, quote_EditTxt, page_EditTxt, author_EditTxt;
    private Spinner category_spinner;
    private FitButton ok_btn, cancel_btn, add_category_btn, quote_author_btn, edit_category_btn,
            delete_category_btn, camera_btn, favourite_btn;

    private Quote thisQuote = null;
    private Quote latestQuote;
    private QuoteCategory newCategory;

    private String bookId;
    private String chapterId = "";

    private boolean inEdition = false;
    private boolean favourite = false;

    private QuoteCategorySpinnerAdapter spinnerAdapter;
    private List<QuoteCategory> categories = new ArrayList<>();

    private Uri imgUri;

    private boolean selectNewCategory = false;

    private int backCounter = 0;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadDialogTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_quote);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);

        initWidgets();
        setOnClickListeners();
        loadFromIntent();

        spinnerAdapter = new QuoteCategorySpinnerAdapter(this);

        QuoteCategoryViewModel categoryViewModel = new ViewModelProvider(this).get(QuoteCategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(this, quoteCategories -> {
            if (quoteCategories.size() == 0) { // if basic category isn't created
                QuoteCategory qc = new QuoteCategory("Uncategorized", getString(R.string.uncategorized), Markers.BLUE_START_COLOR);
                categoryViewModel.insert(qc);
                quoteCategories.add(qc);
            }

            categories.clear();
            categories.addAll(quoteCategories);

            spinnerAdapter.setCategories(categories);
            category_spinner.setAdapter(spinnerAdapter);

            //set spinner's selection on last used category
            if (latestQuote != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId().equals(latestQuote.getCategoryId())) {
                        category_spinner.setSelection(i);
                        break;
                    }
                }
            }

            if (selectNewCategory) { // if we created a new QuoteCategory -> select it
                category_spinner.setSelection(spinnerAdapter.getItemPosition(newCategory.getName()));
                selectNewCategory = false;
            }
        });
    }

    private void loadFromIntent() {
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        latestQuote = (Quote) intent.getSerializableExtra("latestQuote");

        if (intent.hasExtra("quote")) {
            thisQuote = (Quote) getIntent().getSerializableExtra("quote");
            bookId = thisQuote.getBookId();
            inEdition = true;
            loadQuoteData(thisQuote);
        }

        if (bookId.isEmpty()) {
            quote_author_btn.setVisibility(View.GONE);
        }
    }

    private void initWidgets() {
        title_EditTxt = findViewById(R.id.add_quote_title_EditTxt);
        author_EditTxt = findViewById(R.id.add_quote_author_EditTxt);
        quote_EditTxt = findViewById(R.id.add_quote_quote_EditTxt);
        page_EditTxt = findViewById(R.id.add_quote_page_EditTxt);
        category_spinner = findViewById(R.id.add_quote_category_spinner);
        add_category_btn = findViewById(R.id.add_quote_add_category_btn);
        edit_category_btn = findViewById(R.id.add_quote_edit_category_btn);
        delete_category_btn = findViewById(R.id.add_quote_delete_category_btn);
        favourite_btn = findViewById(R.id.favourite_btn);
        ok_btn = findViewById(R.id.add_quote_ok_btn);
        cancel_btn = findViewById(R.id.add_quote_cancel_btn);
        quote_author_btn = findViewById(R.id.quote_author_btn);
        camera_btn = findViewById(R.id.camera_btn);
    }

    private void loadQuoteData(@NotNull Quote quote) {
        title_EditTxt.setText(quote.getTitle());
        quote_EditTxt.setText(quote.getQuote());
        author_EditTxt.setText(quote.getAuthor());
        page_EditTxt.setText(String.valueOf(quote.getPage()));
        chapterId = quote.getChapterId();
        favourite = quote.isFavourite();

        if (favourite)
            favourite_btn.setIcon(Objects.requireNonNull(ContextCompat.getDrawable(this, R.mipmap.favourite_star)));

        //set spinner selection on thisQuote's category
        (new Handler()).postDelayed(() -> {
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(thisQuote.getCategoryId());
                System.out.println(categories.get(i).getId());
                if (thisQuote.getCategoryId().equals(categories.get(i).getId())) {
                    category_spinner.setSelection(i);
                    break;
                }
            }
        }, 500);

    }

    private void setOnClickListeners() {

        add_category_btn.setOnClickListener(view ->
                startActivityForResult(new Intent(this, AddQuoteCategoryActivity.class),
                        Requests.ADD_REQUEST));

        edit_category_btn.setOnClickListener(view -> {
            QuoteCategory qc = ((QuoteCategory) (spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())));

            if (!qc.getId().equals("Uncategorized")) {
                startActivityForResult(new Intent(this, AddQuoteCategoryActivity.class)
                        .putExtra("category", qc), Requests.ADD_REQUEST);
            } else {
                Toast.makeText(this, getString(R.string.basic_category), Toast.LENGTH_SHORT).show();
            }
        });

        delete_category_btn.setOnClickListener(view -> {
            QuoteCategory qc = ((QuoteCategory) (spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())));

            if (!qc.getId().equals("Uncategorized")) {
                DeletingManager deletingManager = new DeletingManager(this);
                deletingManager.showDeletingDialog(getString(R.string.del_quote_category),
                        getString(R.string.delete_quote_category),
                        DeletingManager.QUOTECATEGORY,
                        qc);
            } else {
                Toast.makeText(this, getString(R.string.basic_category), Toast.LENGTH_SHORT).show();
            }
        });

        ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                QuoteViewModel viewModel = new ViewModelProvider(AddQuoteActivity.this).get(QuoteViewModel.class);
                if (inEdition) {
                    viewModel.update(thisQuote);
                } else {
                    viewModel.insert(thisQuote);
                }
                finish();
            }
        });

        cancel_btn.setOnClickListener(view -> finish());

        quote_author_btn.setOnClickListener(view -> {
            BookViewModel bookViewModel = new ViewModelProvider(AddQuoteActivity.this).get(BookViewModel.class);
            bookViewModel.getBook(bookId).observe(AddQuoteActivity.this, book -> {
                if (book != null) {
                    author_EditTxt.setText(book.getAuthor());
                }
            });
        });

        camera_btn.setOnClickListener(view -> {
            try {
                if (!PermissionsManager.checkCameraPermission(this)) {
                    PermissionsManager.requestCameraPermission(this);
                } else {
                    imgUri = IntentManager.setUpImageOutputUri(this);
                    IntentManager.pickCamera(this, imgUri);
                }
            } catch (Exception exc) {
                Toast.makeText(this, getString(R.string.recognition_error), Toast.LENGTH_LONG).show();
                Log.e(TAG, "setOnClickListeners: camera", exc);
            }
        });

        favourite_btn.setOnClickListener(view -> {
            favourite = !favourite;
            favourite_btn.setIcon(Objects.requireNonNull(
                    ContextCompat.getDrawable(this, favourite ? R.mipmap.favourite_star : R.mipmap.favourite_star_off)));

        });
    }

    private boolean areValidOutputs() {

        String title, quote, id, categoryId, author;
        int page = 0;

        //isQuoteShorterThan2
        if (quote_EditTxt.getText().length() < 2) {
            Toast.makeText(this, getString(R.string.quote_short), Toast.LENGTH_SHORT).show();
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
            author = author_EditTxt.getText().toString();
            quote = quote_EditTxt.getText().toString();
            categoryId = ((QuoteCategory) spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())).getId();
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }

        //isPageANumber -> getPage
        if (page_EditTxt.length() > 0) {
            try {
                page = Integer.valueOf(page_EditTxt.getText().toString());
            } catch (NumberFormatException exc) {
                Toast.makeText(this, getString(R.string.type_pages_as_a_number), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        thisQuote = new Quote(id, quote, title, author, categoryId, page, bookId);
        thisQuote.setChapterId(chapterId);
        thisQuote.setFavourite(favourite);

        return true;
    }

    /**
     * Handles the permissionsResult and if permitted -> picks camera
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionsManager.handlePermissionsResult(this, requestCode, grantResults)) { //here its always camera permission request
            imgUri = IntentManager.setUpImageOutputUri(this);
            IntentManager.pickCamera(this, imgUri);
        } else {
            Toast.makeText(this, getString(R.string.need_permissions), Toast.LENGTH_SHORT).show();
        }
    }

    //handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == IntentManager.PICK_CAMERA_CODE) {
                    IntentManager.pickCropper(this, imgUri, 50, 50);

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);

                        //Recognize the text
                        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(bitmap);
                        FirebaseVisionTextRecognizer deviceRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                        deviceRecognizer.processImage(visionImage)
                                .addOnSuccessListener(firebaseVisionText ->
                                        quote_EditTxt.setText(firebaseVisionText
                                                .getText()
                                                .replace('\n', ' ')
                                                .replace("Č", "Ć")
                                                .replace("č", "ć")
                                                .replace("Š", "S")
                                                .replace("š", "s")
                                                .replace("Ž", "Ż")
                                                .replace("ž", "ż")
                                        )
                                )
                                .addOnFailureListener(Throwable::printStackTrace);


                        new File(IntentManager.getRealPath(this, imgUri)).delete();
                        (new Handler()).postDelayed(() -> quote_EditTxt.invalidate(), 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (requestCode == Requests.ADD_REQUEST && data != null) {
                    newCategory = (QuoteCategory) data.getSerializableExtra("category");
                    selectNewCategory = true;
                }
            }
        } catch (Exception exc) {
            Toast.makeText(this, getString(R.string.recognition_error),
                    Toast.LENGTH_SHORT).show();
            exc.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCounter == 0) {
                backCounter = 1;
                (new Handler()).postDelayed(() -> backCounter = 0, 2000);
            } else {
                backCounter = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
