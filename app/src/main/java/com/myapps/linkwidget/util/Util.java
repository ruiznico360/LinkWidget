package com.myapps.linkwidget.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;

public class Util {
    public static void log(Object... args) {
        String s = "";
        for (Object p : args) {
            s += p + " ";
        }
        Log.i("MyWidget", s);
    }

    public static void launchUrl(Uri parsed, Context c) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, parsed);
        c.startActivity(browserIntent);
    }
}