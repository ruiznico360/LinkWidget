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
import com.myapps.linkwidget.util.UrlUtil;
import com.myapps.linkwidget.util.Util;

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
        private Bitmap[] bmaps;
        private Thread[] loadBmaps;

        public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
            this.mContext = applicationContext;
            this.widgetID = intent.getExtras().getInt(Widget.WIDGET_ID);
            load();
        }

        private void load() {
            this.storage = FileIO.getStorage(getApplicationContext());
            this.current = storage.getCurrent(widgetID);

            bmaps = new Bitmap[getCount()];
            loadBmaps = new Thread[getCount()];
            for (int i = 0; i < getCount(); i++) {
                final int index = i;
                loadBmaps[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (current.getStorable(index) instanceof MUrl) bmaps[index] = UrlUtil.getURLIcon(((MUrl)current.getStorable(index)).getUrl());
                    }
                });
                loadBmaps[i].start();
            }
        }

        public RemoteViews getViewAt(int position) {
            if (storage == null) return null;

            final RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_storable);
            Intent fillInIntent = new Intent();
            Bundle b = IntentHandler.genDefaultBundle(widgetID);

            if (current.getStorable(position) instanceof MFolder) {
                MFolder l = (MFolder) current.getStorable(position);
                rv.setTextViewText(R.id.linkText, l.getName());
                rv.setImageViewResource(R.id.linkLogo, R.drawable.folder_mini);
                b.putSerializable(IntentHandler.CHANGE_FOLDER, new IntentHandler.FolderChangeStorer(storage, l));
            }else{
                final MUrl l = (MUrl) current.getStorable(position);
                rv.setTextViewText(R.id.linkText, l.getName());
                b.putString(IntentHandler.URL_TO_VIEW, l.getUrl());

                try {
                    loadBmaps[position].join(2000);
                }catch (InterruptedException e) {}

                if (bmaps[position] == null) rv.setImageViewResource(R.id.linkLogo, R.drawable.internet_mini);
                else rv.setImageViewBitmap(R.id.linkLogo, bmaps[position]);
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
