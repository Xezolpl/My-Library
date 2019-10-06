package pl.xezolpl.mylibrary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import pl.xezolpl.mylibrary.R;

public class AddBookDialog extends AppCompatDialogFragment {
    private EditText add_book_title,add_book_author,add_book_description,add_book_pages;
    private Button select_image_btn;
    private ImageView add_book_image;
    private Spinner status_spinner;
    private View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_add_book,null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: GET DATA FROM EDIT TEXTS AND ADD BOOK INTO THE DATABASE
                    }
                });
        initWidgets();
        setUpStatusSpinner();
        return builder.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    private void initWidgets(){
        add_book_title = (EditText) view.findViewById(R.id.add_book_title);
        add_book_author = (EditText) view.findViewById(R.id.add_book_author);
        add_book_description = (EditText) view.findViewById(R.id.add_book_description);
        add_book_pages = (EditText) view.findViewById(R.id.add_book_pages);
        add_book_image = (ImageView) view.findViewById(R.id.add_book_image);
        select_image_btn = (Button) view.findViewById(R.id.select_image_btn);
        status_spinner = (Spinner) view.findViewById(R.id.status_spinner);
    }
    private void setUpStatusSpinner(){
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_array,android.R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusArrayAdapter);
    }
}
