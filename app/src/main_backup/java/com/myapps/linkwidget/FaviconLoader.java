package com.myapps.linkwidget;

import android.content.Context;
import android.webkit.WebView;

public class FaviconLoader extends WebView {
    protected Url currentLoading;

    public FaviconLoader(Context c) {
        super(c);
    }

    public void loadUrl(Url url) {
        this.currentLoading = url;
        super.loadUrl(Url.format(url.url));
    }
}
