package com.myapps.linkwidget;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UrlLauncher extends Activity {
    public static final String URL_TO_VIEW = "URL_TO_VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        Uri parsed = Url.parse(getIntent().getExtras().getString(URL_TO_VIEW));

        if (parsed == null) {
            Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
        }else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, parsed);
            startActivity(browserIntent);
        }
    }
}
