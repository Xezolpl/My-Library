package pl.xezolpl.mylibrary.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;

public class SimpleRecViewAdapter extends RecyclerView.Adapter<SimpleRecViewAdapter.ViewHolder> {

    private List<String> list = new ArrayList<>();
    private Context context;
    private int enumerator = DOT_ENUMERATOR;
    private int color = Color.BLACK;

    public static final int DOT_ENUMERATOR = R.drawable.color_dot; //todo: change it to the resource's ID
    public static final int DASH_ENUMERATOR = 1;
    public static final int LETTER_ENUMERATOR = 2;

    public SimpleRecViewAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setListStyle(int enumerator, int color) {
        this.enumerator = enumerator;
        this.color = color;
    }

    public List<String> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(list.get(position), enumerator, color);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText editText;
        private ImageView imgView;
        private int position=-1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.listItem_edtTxt);
            imgView = (ImageView) itemView.findViewById(R.id.listItem_imgView);
            ((GradientDrawable) imgView.getBackground()).setColor(Color.BLACK);
        }

        public void setData(String text, int enumerator, int color) {
            editText.setText(text);
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, enumerator);
            drawable.setColor(color);
            imgView.setBackground(drawable);

            if(position==-1){
                for(int j=0;j<list.size();j++){
                    if(text.equals(list.get(j))) position = list.indexOf(list.get(j));
                }
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        list.set(position,editable.toString());
                    }catch (ArrayIndexOutOfBoundsException exc){
                        exc.printStackTrace();
                    }

                }
            });
        }
    }
}
