package pl.xezolpl.mylibrary.utilities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {

    public static final float MINI_TEXT_SIZE = 24f;
    public static final float SMALL_TEXT_SIZE = 48f;
    public static final float MEDIUM_TEXT_SIZE = 72f;
    public static final float BIG_TEXT_SIZE = 96f;
    public static final float LARGE_TEXT_SIZE = 120f;
    public static final float HUGE_TEXT_SIZE = 144f;

    private final String text;
    private final Paint paint;

    public TextDrawable(String text) {

        this.text = text;

        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(120f);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setTextSize(float textSize) {
        paint.setTextSize(textSize);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, canvas.getWidth() / 2, canvas.getHeight() - 25, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
