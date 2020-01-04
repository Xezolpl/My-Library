package pl.xezolpl.mylibrary.utilities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;

    private int width;
    private int height;
    private float textSize;

    public TextDrawable(String text) {

        this.text = text;
        paint = new Paint();


        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect r = getBounds();
        width = r.width();
        height = r.height();

        textSize = Math.min(width, height) * 1.15f;
        if (text.charAt(1) == ')') {
            if (textSize > 50) {
                textSize -= 15;
            } else if (textSize > 100) {
                textSize -= 30;
            } else if (textSize > 200) {
                textSize -= 65;
            }
        }
        paint.setTextSize(textSize);


        canvas.drawText(text, getBounds().width() / 2, height / 2f - ((paint.descent() + paint.ascent()) / 2), paint);
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
