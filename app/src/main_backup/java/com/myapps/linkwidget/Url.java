package com.myapps.linkwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.URLUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class Url implements Serializable {
    protected String name, url, bitmapID;

    protected Url(String name, String url) {
        this.name = name;
        this.url = url;
        this.bitmapID = "INVALID";
    }

    public static Uri parse(String url) {
        return Uri.parse(format(url));
    }

    protected static String format(String url) {
        if (!URLUtil.isValidUrl(url)) {
            url = "http://" + url;
        }
        return url;
    }

    public static Bitmap getURLIcon(String uri) {
        String url = format(uri);
        try
        {
            InputStream input = (InputStream) new java.net.URL(url + "/favicon.ico").getContent();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) { }

        return null;
    }
}