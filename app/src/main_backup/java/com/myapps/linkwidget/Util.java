package com.myapps.linkwidget;

import android.util.Log;

public class Util {
    public static void log(Object... args) {
        String s = "";
        for (Object p : args) {
            s += p + " ";
        }
        Log.i("MyWidget", s);
    }
}
