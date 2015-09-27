package me.rorschach.myinstagram.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by hl810 on 15-9-8.
 */
public class CircleTransformation implements Transformation {

    private static final int STORKE_WIDTH = 6;

    @Override
    public Bitmap transform(Bitmap source) {

        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squareBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint avatarPaint = new Paint();
        BitmapShader shader = new BitmapShader(
                squareBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        avatarPaint.setShader(shader);

        Paint outlinePaint = new Paint();
        outlinePaint.setColor(Color.WHITE);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(STORKE_WIDTH);
        outlinePaint.setAntiAlias(true);

        float r = size / 2;
        canvas.drawCircle(r, r, r, avatarPaint);
        canvas.drawCircle(r, r, r - STORKE_WIDTH / 2, outlinePaint);

        squareBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "CircleTransformation";
    }
}
