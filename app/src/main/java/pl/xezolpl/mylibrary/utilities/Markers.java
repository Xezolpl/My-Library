package pl.xezolpl.mylibrary.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Objects;

import pl.xezolpl.mylibrary.R;

public abstract class Markers {

    public static final int BLUE_START_COLOR = Color.parseColor("#51C1ED");

    //SIMPLE MARKERS
    private static final int DOT_MARKER = R.drawable.color_dot;
    private static final int DASH_MARKER = R.drawable.color_dash;
    private static final int STAR_MARKER = R.drawable.color_star;

    public static Drawable getSimpleMarker(Context context, int marker, int color) throws IOException {
        if (marker == DOT_MARKER || marker == DASH_MARKER || marker == STAR_MARKER) {
            GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(context, marker);
            Objects.requireNonNull(drawable, "Drawable was null inside markers").setColor(color);
            return drawable;
        } else throw new IOException();
    }


    //LETTER_MARKER MARKERS - LETTERS, NUMBERS OR OTHER CHARS
    public static final int LETTER_MARKER = 10;
    public static final int NUMBER_MARKER = 11;

    private static final String[] alphabet = new String[]{"a)", "b)", "c)", "d)", "e)", "f)", "g)", "h)", "i)"
            , "j)", "k)", "l)", "m)", "n)", "o)", "p)", "q)", "r)", "s)", "t)", "u)", "v)", "w)", "x)", "y)", "z)"};


    public static Drawable getLetterMarker(int markerType, int markerPosition, int color) throws IOException {
        String text;

        if (markerType == LETTER_MARKER) {
            text = alphabet[markerPosition];
        } else if (markerType == NUMBER_MARKER) {
            text = String.valueOf(markerPosition + 1);
            text += '.';
        } else throw new IOException();


        TextDrawable drawable = new TextDrawable(text);
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        drawable.draw(new Canvas());
        return drawable;
    }

    //Methods
    public static int incrementMarker(int marker) {
        switch (marker) {
            case NUMBER_MARKER: {
                marker = LETTER_MARKER;
                break;
            }
            case LETTER_MARKER: {
                marker = DOT_MARKER;
                break;
            }
            case DOT_MARKER: {
                marker = DASH_MARKER;
                break;
            }
            case DASH_MARKER: {
                marker = STAR_MARKER;
                break;
            }
            case STAR_MARKER: {
                marker = NUMBER_MARKER;
                break;
            }
            default:
                break;
        }
        return marker;

    }

}
