package uk.co.pilllogger.helpers;

import java.util.Random;

/**
 * Created by alex on 13/11/2013.
 */
public class NumberHelper {
    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String getNiceFloatString(float value){
        if(value - (int)value == 0){
            return String.valueOf((int)value);
        }

        return String.valueOf(value);
    }
}
