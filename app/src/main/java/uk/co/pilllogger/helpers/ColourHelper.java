package uk.co.pilllogger.helpers;

import android.graphics.Color;

/**
 * Created by Alex on 08/03/14.
 */
public class ColourHelper {
    public static int getDarker(int colour){
        float[] hsv = new float[3];
        Color.colorToHSV(colour, hsv);
        hsv[2] *= 0.5f; // value component

        return Color.HSVToColor(hsv);
    }

    public static int getLighter(int colour){
        float[] hsv = new float[3];
        Color.colorToHSV(colour, hsv);
        hsv[2] /= 0.5f; // value component

        return Color.HSVToColor(hsv);
    }

    public static boolean isColourLight(int colour) {
        float[] hsv = new float[3];
        Color.colorToHSV(colour, hsv);

        if (hsv[2] > 0.9)
            return true;
        return false;
    }
}
