package uk.co.pilllogger.helpers;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 03/12/2013.
 */
public class ArrayHelper {
    public static String StringArrayToString(String[] array){
        return StringArrayToString(Arrays.asList(array));
    }

    public static String StringArrayToString(List<String> array){
        StringBuilder sb = new StringBuilder();
        for (String n : array) {
            if (sb.length() > 0) sb.append(',');
            sb.append("").append(n).append("");
        }
        return sb.toString();
    }
}
