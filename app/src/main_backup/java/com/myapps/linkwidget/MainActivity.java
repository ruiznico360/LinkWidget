package com.myapps.linkwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout lv;
    private boolean saving = false;
    private ArrayList<View> editors = new ArrayList<>();
    private int INFLATE_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        INFLATE_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        lv = findViewById(R.id.mainList);

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View newInflated = generate(null);


                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((ScrollView)findViewById(R.id.mainListScroller)).fullScroll(View.FOCUS_DOWN);
                        newInflated.findViewById(R.id.nameEditor).requestFocus();
                    }
                });
            }
        });

        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commenceSave();
            }
        });

        for (Url u : FileIO.loadUrlsFromSave(getApplicationContext())) {
            generate(u);
        }
    }

    private void commenceSave() {
        saving = true;
        closeKeyBoard();

        final ArrayList<Url> urls = new ArrayList<>();
        final ArrayList<Bitmap> bmps = new ArrayList<>();

        for (int i = 0; i < editors.size(); i++) {
            View r = editors.get(i);
            String name = ((EditText)r.findViewById(R.id.nameEditor)).getText().toString();
            String url = ((EditText)r.findViewById(R.id.urlEditor)).getText().toString();

            if (name.equals("") || url.equals("")) {
                Toast.makeText(getApplicationContext(), "Make sure no fields are empty!", Toast.LENGTH_LONG).show();
                return;
            }

            Url u = new Url(name, url);
            urls.add(u);
        }

        FileIO.prepareBitmapdirectory(getApplicationContext());

        for (int i = 0; i < urls.size(); i++) {
            final int index = i;

            RelativeLayout parent = findViewById(R.id.listHeader);
            final FaviconLoader faviconLoader = new FaviconLoader(getApplicationContext());
            faviconLoader.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
            parent.addView(faviconLoader, params);

            faviconLoader.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (index == urls.size() - 1) {
                        finishSave(urls, bmps);
                    }
                    faviconLoader.destroy();
                }

            });

            faviconLoader.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    bmps.add(icon);
                    faviconLoader.currentLoading.bitmapID = icon.toString();

                    if (index == urls.size() - 1) {
                        finishSave(urls, bmps);
                    }
                    faviconLoader.destroy();
                }
            });

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    faviconLoader.loadUrl(urls.get(index));
                }
            });
        }
    }

    private void finishSave(ArrayList<Url> urls, ArrayList<Bitmap> bmps) {
        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
        FileIO.saveUrls(urls, getApplicationContext());
        FileIO.saveBitmaps(bmps, getApplicationContext());

        ComponentName name = new ComponentName(getApplicationContext(), LinksWidget.class);
        int [] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(name);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.link_list);
        saving = false;
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (saving) Toast.makeText(getApplicationContext(), "Save could not be completed.", Toast.LENGTH_LONG).show();

        finish();
    }

    protected void closeKeyBoard() {
        if (getCurrentFocus() != null) ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }


    private View generate(Url tag) {
        final View newInflated = getLayoutInflater().inflate(R.layout.activity_main_storable, null, false);
        newInflated.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(newInflated);
                closeKeyBoard();
            }
        });

        if (tag != null) {
            ((EditText) newInflated.findViewById(R.id.nameEditor)).setText(tag.name);
            ((EditText) newInflated.findViewById(R.id.urlEditor)).setText(tag.url);
        }

        editors.add(newInflated);

        refresh();
        return newInflated;
    }

    private void remove(View v) {
        editors.remove(v);

        refresh();
    }

    private void refresh() {
        lv.removeAllViews();
        for (int i = 0; i < editors.size(); i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

//                params.addRule(RelativeLayout.BELOW, bottomID);
//
//                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
//
//                params.topMargin = -(int)px;

            params.topMargin = i * INFLATE_HEIGHT;
            lv.addView(editors.get(i), params);
        }
    }
}
