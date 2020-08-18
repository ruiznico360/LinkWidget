package com.myapps.linkwidget.util;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.myapps.linkwidget.R;
import com.myapps.linkwidget.mainui.MainActivity;
import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.serialize.FileIO;
import com.myapps.linkwidget.serialize.Storage;
import com.myapps.linkwidget.widget.LinksWidgetService;
import com.myapps.linkwidget.widget.Widget;

import java.util.Random;

public class WidgetUtil {
    public static void updateWidgetData(Context context, int[] widgetIDs) {
        Storage s = FileIO.getStorage(context);
        for (int id : widgetIDs) {
            MFolder current = s.getCurrent(id);

            if (current == null) {
                current = s.getBase();
                s.getWidgetCurrents().put(id, current);
                FileIO.saveStorage(s, context);
            }
            int flag = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT;


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);

            Intent titleIntent = new Intent(context, MainActivity.class);
            titleIntent.putExtra(Widget.WIDGET_ID, id);
            PendingIntent titlePendingIntent = PendingIntent.getActivity(context, id, titleIntent, flag);

            remoteViews.setOnClickPendingIntent(R.id.editButton, titlePendingIntent);
            remoteViews.setTextViewText(R.id.title, s.getCurrent(id).getName());

            Intent intent = new Intent(context, LinksWidgetService.class);
            intent.putExtra(Widget.WIDGET_ID, id);
            Random rnd = new Random();
            intent.setData(Uri.fromParts("content", String.valueOf(rnd.nextInt()), null));
            remoteViews.setRemoteAdapter(R.id.link_list, intent);

            if (s.isViewingBase(id)) {
                remoteViews.setImageViewResource(R.id.mainIconButton, R.drawable.firefox_logo);
                remoteViews.setOnClickPendingIntent(R.id.mainIconButton, null);
            }else{
                Intent fillInIntent = new Intent(context, IntentHandler.class);
                Bundle b = IntentHandler.genDefaultBundle(id);

                b.putSerializable(IntentHandler.CHANGE_FOLDER, new IntentHandler.FolderChangeStorer(s, current.getParent()));
                fillInIntent.putExtras(b);
                PendingIntent pe = PendingIntent.getActivity(context, id, fillInIntent, flag);

                remoteViews.setImageViewResource(R.id.mainIconButton, R.drawable.back);
                remoteViews.setOnClickPendingIntent(R.id.mainIconButton, pe);
            }

            Intent clickIntentTemplate = new Intent(context, IntentHandler.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context).addNextIntentWithParentStack(clickIntentTemplate).getPendingIntent(0, flag);

            remoteViews.setEmptyView(R.id.link_list, R.id.empty);
            remoteViews.setOnClickPendingIntent(R.id.empty, titlePendingIntent);
            remoteViews.setPendingIntentTemplate(R.id.link_list, clickPendingIntentTemplate);
            appWidgetManager.updateAppWidget(id, remoteViews);
        }
    }

    public static void sendUpdate(Context c) {
        ComponentName name = new ComponentName(c, Widget.class);
        int [] ids = AppWidgetManager.getInstance(c).getAppWidgetIds(name);

//        updateList(c);
        updateWidgetData(c, ids);
    }

    public static void updateList(Context c) {
        ComponentName name = new ComponentName(c, Widget.class);
        int [] ids = AppWidgetManager.getInstance(c).getAppWidgetIds(name);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.link_list);
    }
    public static void clear(Context c, int[] widgetIDs) {
        Storage s = FileIO.getStorage(c);
        for (int id : widgetIDs) {
            s.getWidgetCurrents().remove(id);
        }
        FileIO.saveStorage(s, c);
    }
}
