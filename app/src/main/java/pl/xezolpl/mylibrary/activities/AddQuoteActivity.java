package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.github.nikartm.button.FitButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.QuoteCategorySpinnerAdapter;
import pl.xezolpl.mylibrary.managers.DeletingManager;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.PermissionsManager;
import pl.xezolpl.mylibrary.models.Quote;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;
import pl.xezolpl.mylibrary.viewmodels.QuoteViewModel;

public class AddQuoteActivity extends AppCompatActivity {

    private MaterialEditText title_EditTxt, quote_EditTxt, page_EditTxt, author_EditTxt;
    private Spinner category_spinner;
    private FitButton ok_btn, cancel_btn, add_category_btn, quote_author_btn, edit_category_btn, delete_category_btn, camera_btn;

    private Quote thisQuote = null;
    private String bookId;
    private String chapterId = "";

    private boolean inEdition = false;

    public static final int ADD_QUOTE_CATEGORY_REQUEST_CODE = 7;

    private QuoteCategoryViewModel categoryViewModel;
    private QuoteCategorySpinnerAdapter spinnerAdapter;
    private List<QuoteCategory> categories = new ArrayList<>();

    private Uri imgUri;

    private boolean selectNewCategory = false;
    private QuoteCategory newCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setOnClickListeners();
        setFinishOnTouchOutside(false);
        loadFromIntent();

        spinnerAdapter = new QuoteCategorySpinnerAdapter(this);
        categoryViewModel = ViewModelProviders.of(this).get(QuoteCategoryViewModel.class);

        categoryViewModel.getAllCategories().observe(this, quoteCategories -> {
            if (quoteCategories.size() == 0) {
                String uncategorized = getString(R.string.uncategorized);

                QuoteCategory qc = new QuoteCategory(uncategorized, uncategorized, Markers.BLUE_START_COLOR);
                categoryViewModel.insert(qc);
                quoteCategories.add(qc);
            }
            categories = quoteCategories;
            spinnerAdapter.setCategories(categories);
            category_spinner.setAdapter(spinnerAdapter);

            if (inEdition) loadQuoteData(thisQuote);

            if(selectNewCategory){
                category_spinner.setSelection(spinnerAdapter.getItemPosition(newCategory.getName()));
                selectNewCategory = false;
            }
        });
    }

    private void loadFromIntent() {
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        if (intent.hasExtra("quote")) {
            thisQuote = (Quote) getIntent().getSerializableExtra("quote");
            bookId = thisQuote.getBookId();
            inEdition = true;
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

        for (int i=0; i<categories.size(); i++){
            if (thisQuote.getCategoryId().equals(categories.get(i).getId())){
                category_spinner.setSelection(i);
                break;
            }
        }
    }

    private void setOnClickListeners() {

        add_category_btn.setOnClickListener(view -> {
            Intent intent = new Intent(AddQuoteActivity.this, AddQuoteCategoryActivity.class);
            startActivityForResult(intent, ADD_QUOTE_CATEGORY_REQUEST_CODE);
        });
        edit_category_btn.setOnClickListener(view -> {
            QuoteCategory qc = ((QuoteCategory) (spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())));

            if (!qc.getId().equals(getString(R.string.uncategorized))) {
                Intent intent = new Intent(AddQuoteActivity.this, AddQuoteCategoryActivity.class);
                intent.putExtra("category", qc);
                startActivityForResult(intent, ADD_QUOTE_CATEGORY_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Its basic category, you cannot edit or delete it.", Toast.LENGTH_SHORT).show();
            }
        });

        delete_category_btn.setOnClickListener(view -> {
            QuoteCategory qc = ((QuoteCategory) (spinnerAdapter.getItem(category_spinner.getSelectedItemPosition())));

            if (!qc.getId().equals(getString(R.string.uncategorized))) {
                DeletingManager deletingManager = new DeletingManager(this);
                deletingManager.showDeletingDialog(getString(R.string.del_quote_category),
                        getString(R.string.delete_quote_category),
                        DeletingManager.QUOTECATEGORY,
                        qc);
            } else {
                Toast.makeText(this, "Its basic category, you cannot edit or delete it.", Toast.LENGTH_SHORT).show();
            }
        });

        ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                QuoteViewModel viewModel = ViewModelProviders.of(AddQuoteActivity.this).get(QuoteViewModel.class);
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
            BookViewModel bookViewModel = ViewModelProviders.of(AddQuoteActivity.this).get(BookViewModel.class);
            bookViewModel.getBook(bookId).observe(AddQuoteActivity.this, book -> author_EditTxt.setText(book.getAuthor()));
        });

        camera_btn.setOnClickListener(view -> {
            if (!PermissionsManager.checkCameraPermission(this)) {
                PermissionsManager.requestCameraPermission(this);
            } else {
                imgUri = IntentManager.setUpOutputUri(this);
                IntentManager.pickCamera(this, imgUri);
            }
        });
    }

    private boolean areValidOutputs() {

        String title, quote, id, categoryId, author;
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
                Toast.makeText(this, "Type pages as a number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        thisQuote = new Quote(id, quote, title, author, categoryId, page, bookId);
        thisQuote.setChapterId(chapterId);
        return true;
    }

    //handle permissions results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsManager.CAMERA_REQUEST) {
            if (grantResults.length > 0) {

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (cameraAccepted && writeStorageAccepted) {
                    imgUri = IntentManager.setUpOutputUri(this);
                    IntentManager.pickCamera(this, imgUri);
                }
            }
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
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    //RECOGNITION TEXT ON A IMAGE
                    FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionTextRecognizer cloudRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                    cloudRecognizer.processImage(visionImage)
                            .addOnSuccessListener(firebaseVisionText ->
                                    quote_EditTxt.setText(firebaseVisionText.getText()))
                            .addOnFailureListener(Throwable::printStackTrace);

                    //DELETING IMAGE FILE
                    new File(IntentManager.getRealPath(this, imgUri)).delete();

                } else if (requestCode == ADD_QUOTE_CATEGORY_REQUEST_CODE && data != null) {
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

}
