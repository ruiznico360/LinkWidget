package com.myapps.linkwidget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class LinksWidgetService extends RemoteViewsService {
    private MyWidgetRemoteViewsFactory getViewFactory;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        getViewFactory = new MyWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
        return getViewFactory;
    }

    public class MyWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private List<Url> linkItems;
        private Context mContext;

        public MyWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
            mContext = applicationContext;

            linkItems = FileIO.loadUrlsFromSave(applicationContext);
        }

        public void onCreate() {

        }
        public void onDestroy() {
            linkItems.clear();
        }
        public int getCount() {
            return linkItems.size();
        }

        public RemoteViews getViewAt(int position) {
            Url l = linkItems.get(position);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_storable);
            rv.setTextViewText(R.id.linkText, l.name);

            rv.setImageViewBitmap(R.id.linkLogo, FileIO.loadBitmapsForIndex(mContext, l.bitmapID));


            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(UrlLauncher.URL_TO_VIEW, l.url);
            rv.setOnClickFillInIntent(R.id.linkButton, fillInIntent);

            return rv;
        }
        public RemoteViews getLoadingView() {
            return null;
        }
        public int getViewTypeCount() {
            return 1;
        }
        public long getItemId(int position) {
            return position;
        }
        public boolean hasStableIds() {
            return true;
        }

        public void onDataSetChanged() {
            linkItems = FileIO.loadUrlsFromSave(mContext);
        }

    }
}
