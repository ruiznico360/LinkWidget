package com.myapps.linkwidget.util;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.model.MUrl;
import com.myapps.linkwidget.serialize.FileIO;
import com.myapps.linkwidget.serialize.Storage;
import com.myapps.linkwidget.widget.Widget;

import java.io.Serializable;

public class IntentHandler extends Activity {
    public static final String URL_TO_VIEW = "URL_TO_VIEW";
    public static final String CHANGE_FOLDER = "CHANGE_FOLDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        
        int widgetID = getIntent().getExtras().getInt(Widget.WIDGET_ID);

        if (getIntent().getExtras().get(URL_TO_VIEW) != null) {
            Uri parsed = MUrl.parse(getIntent().getExtras().getString(URL_TO_VIEW));

            if (parsed == null) {
                Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_LONG).show();
            } else {
                Util.launchUrl(parsed, this);
            }

            //cogit
        }else{
            FolderChangeStorer f = (FolderChangeStorer) getIntent().getExtras().getSerializable(IntentHandler.CHANGE_FOLDER);
            setChangeFolder(f.storage, f.toChange, widgetID);
        }
        WidgetUtil.updateWidgetData(getApplicationContext(), new int[]{widgetID});
    }

    private void setChangeFolder(Storage s, MFolder l, int widgetID) {
        s.getWidgetCurrents().put(widgetID, l);
        FileIO.saveStorage(s, getApplicationContext());
    }

    public static class FolderChangeStorer implements Serializable {
        public Storage storage;
        public MFolder toChange;

        public FolderChangeStorer(Storage s, MFolder toChange) {
            this.storage = s;
            this.toChange = toChange;
        }
    }

    public static Bundle genDefaultBundle(int widgetID) {
        Bundle b = new Bundle();
        b.putInt(Widget.WIDGET_ID, widgetID);
        return b;
    }
}
