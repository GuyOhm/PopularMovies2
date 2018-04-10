package com.example.android.popularmovies2.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

public class MoviesUtils {

    /**
     * Method to calculate number of columns according to the screen size
     * credit: https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
     *
     * @param context of the application
     * @return int number of columns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
