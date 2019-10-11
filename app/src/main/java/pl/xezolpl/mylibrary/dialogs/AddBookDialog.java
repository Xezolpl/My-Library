package pl.xezolpl.mylibrary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Book;

public class AddBookDialog extends AppCompatDialogFragment {
    private EditText add_book_title,add_book_author,add_book_description,add_book_pages;
    private Button select_image_btn;
    private ImageView add_book_image;
    private Spinner status_spinner;
    private View view;
    private RecyclerView book_covers_rec_view;

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
                        if(!areValidOutputs()){
                            Toast.makeText(getContext(), "Cannot add book with empty fields!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Book newBook = new Book(add_book_title.getText().toString(),
                                    add_book_author.getText().toString(),
                                    "URL",
                                    add_book_description.getText().toString(),
                                    Integer.valueOf(add_book_pages.getText().toString()),
                                    UUID.randomUUID().toString(),status_spinner.getSelectedItemPosition()); //TODO: PULL IT UP TO ID
                        }
                    }
                });
        initWidgets();
        select_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Dialog with selecting image or to paste image url
                SelectCoverDialog coverDialog = new SelectCoverDialog();
                coverDialog.show(getFragmentManager(),"Select book's cover");

            }
        });
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
        book_covers_rec_view = (RecyclerView) view.findViewById(R.id.book_covers_rec_view);
    }
    private void setUpStatusSpinner(){
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_array,android.R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusArrayAdapter);
    }
    private boolean areValidOutputs(){
        return (add_book_title.length()>0&&
                add_book_author.length()>0&&
                add_book_description.length()>0);
    }
    private void downloadImageFromUri(String address) {
        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e1) {
            url = null;
        }

        URLConnection conn;
        InputStream in;
        Bitmap bitmap;
        try {
            conn = url.openConnection();
            conn.connect();
            in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            bitmap = null;
        }

        if (bitmap != null) {
            add_book_image.setImageBitmap(bitmap);
        }
    }
}
