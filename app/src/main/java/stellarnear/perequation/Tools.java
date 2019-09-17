package stellarnear.perequation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jchatron on 16/02/2018.
 */

public class Tools {

    public Integer toInt(String key) {
        Integer value = 0;
        try {
            value = Integer.parseInt(key);
        } catch (Exception e) {
        }
        return value;
    }

    public List<Integer> toInt(List<String> listKey) {
        List<Integer> list = new ArrayList<>();
        for (String key : listKey) {
            list.add(toInt(key));
        }
        return list;
    }

    public Long toLong(String key) {
        Long value = 0L;
        try {
            value = Long.parseLong(key);
        } catch (Exception e) {
        }
        return value;
    }

    public BigInteger toBigInt(String key) {
        BigInteger value = BigInteger.ZERO;
        try {
            value = new BigInteger(key);
        } catch (Exception e) {
        }
        return value;
    }

    public Boolean toBool(String key) {
        Boolean value = false;
        try {
            value = Boolean.valueOf(key);
        } catch (Exception e) {
        }
        return value;
    }

    public Drawable resize(Context mC, Drawable image, int pixelSizeIcon) {
        Drawable draw=mC.getDrawable(R.drawable.mire_test);
        try {
            Bitmap b = ((BitmapDrawable) image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixelSizeIcon, pixelSizeIcon, false);
            draw =new BitmapDrawable(mC.getResources(), bitmapResized);
        } catch (Exception e) {
            Bitmap b = ((BitmapDrawable) draw).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixelSizeIcon, pixelSizeIcon, false);
            draw =new BitmapDrawable(mC.getResources(), bitmapResized);
            e.printStackTrace();
        }
        return draw;
    }

    public Drawable resize(Context mC, int imageId, int pixel_size_icon) {
        Drawable image = mC.getDrawable(R.drawable.mire_test);
        try {
            image = mC.getDrawable(imageId);
            Bitmap b = ((BitmapDrawable) image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixel_size_icon, pixel_size_icon, false);
            image = new BitmapDrawable(mC.getResources(), bitmapResized);
        } catch (Exception e) {
            Bitmap b = ((BitmapDrawable) image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, pixel_size_icon, pixel_size_icon, false);
            image = new BitmapDrawable(mC.getResources(), bitmapResized);
            e.printStackTrace();
        }
        return image;
    }

    public Drawable changeColor(Context mC,int img_id, String color) {
        Drawable img = mC.getResources().getDrawable(img_id);
        int iColor = Color.parseColor(color);

        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
        return img;
    }

    public Drawable changeColor(Context mC,int img_id, int color) {
        Drawable img = mC.getResources().getDrawable(img_id);
        int iColor = color;

        int red   = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue  = iColor & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
        return img;
    }

    public Drawable convertToGrayscale(Drawable inputDraw) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        Drawable newDraw = inputDraw.mutate();
        newDraw.setColorFilter(filter);
        return newDraw;
    }


    public void customToast(Context mC, String txt, String... modeInput) {
        // Set the toast and duration
        String mode = modeInput.length > 0 ? modeInput[0] : "";
        Toast mToastToShow = Toast.makeText(mC, txt, Toast.LENGTH_LONG);

        if (mode.contains("center")) {
            TextView v = (TextView) mToastToShow.getView().findViewById(android.R.id.message);
            if (v != null) v.setGravity(Gravity.CENTER);
        }
        mToastToShow.setGravity(Gravity.CENTER, 0, 0);
        mToastToShow.show();

    }


    public Double toDouble(String key) {
        Double value;
        try {
            value = Double.parseDouble(key);
        } catch (Exception e){
            value=0.0;
        }
        return value;
    }
}
