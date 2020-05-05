package com.myapps.linkwidget.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.util.IntentHandler;
import com.myapps.linkwidget.R;
import com.myapps.linkwidget.model.MUrl;
import com.myapps.linkwidget.serialize.FileIO;
import com.myapps.linkwidget.serialize.Storage;

public class LinksWidgetService extends RemoteViewsService {


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Storage storage;
        private Context mContext;
        private MFolder current;
        private int widgetID;

        public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
            this.mContext = applicationContext;
            this.widgetID = intent.getExtras().getInt(Widget.WIDGET_ID);
            load();
        }

        private void load() {
            this.storage = FileIO.getStorage(getApplicationContext());
            this.current = storage.getCurrent(widgetID);
        }

        public RemoteViews getViewAt(int position){
            if (storage == null) return null;

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_storable);
            Intent fillInIntent = new Intent();
            Bundle b = IntentHandler.genDefaultBundle(widgetID);

            if (current.getStorable(position) instanceof MFolder) {
                MFolder l = (MFolder) current.getStorable(position);
                rv.setTextViewText(R.id.linkText, l.getName());
                rv.setImageViewResource(R.id.linkLogo, R.drawable.folder);
                b.putSerializable(IntentHandler.CHANGE_FOLDER, new IntentHandler.FolderChangeStorer(storage, l));
            }else{
                MUrl l = (MUrl) current.getStorable(position);
                rv.setTextViewText(R.id.linkText, l.getName());
                Bitmap icon = MUrl.getURLIcon(l.getUrl());

                if (icon == null){
                    rv.setImageViewResource(R.id.linkLogo, R.drawable.internet);
                }else{
                    rv.setImageViewBitmap(R.id.linkLogo, icon);
                }

                b.putString(IntentHandler.URL_TO_VIEW, l.getUrl());
            }
            fillInIntent.putExtras(b);
            rv.setOnClickFillInIntent(R.id.linkButton, fillInIntent);

            return rv;
        }


        public void onDataSetChanged() { load(); }
        public int getCount() { return current.getSize(); }
        public void onCreate() { }
        public void onDestroy() { storage = null; }
        public RemoteViews getLoadingView() { return new RemoteViews(mContext.getPackageName(), R.layout.empty); }
        public int getViewTypeCount() { return 1; }
        public long getItemId(int position) { return position; }
        public boolean hasStableIds() { return true; }
    }
}
