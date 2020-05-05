package com.myapps.linkwidget.mainui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.serialize.FileIO;
import com.myapps.linkwidget.R;
import com.myapps.linkwidget.serialize.Storage;
import com.myapps.linkwidget.widget.Widget;
import com.myapps.linkwidget.util.WidgetUtil;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Storage storage;
    private MFolder currentWidgetFolder;
    private ActivityHandler handler;
    protected Snackbar undoSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       getBackButton().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               getActivityHandler().backPressed();
           }
       });

       storage = FileIO.getStorage(getApplicationContext());

       if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Widget.WIDGET_ID)) {
           currentWidgetFolder = storage.getCurrent(getIntent().getExtras().getInt(Widget.WIDGET_ID));
       }else {
           currentWidgetFolder = storage.getBase();
       }

       this.handler = new StorableListHandler(this, storage);
       handler.setEnabled(true);
       handler.display();
    }

    @Override
    public void onBackPressed() {
        if (handler.getEnabled() && !handler.backPressed()) super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();

        HashMap<Integer, MFolder> map = new HashMap<>();

        for (int i : storage.getWidgetCurrents().keySet()) {
            if (storage.getWidgetCurrents().get(i).isDeleted()) {
                map.put(i, storage.getBase());
            }else{
                map.put(i, storage.getWidgetCurrents().get(i));
            }
        }
        storage.setWidgetCurrents(map);

        FileIO.saveStorage(storage, getApplicationContext());
        WidgetUtil.sendUpdate(getApplicationContext());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!handler.getEnabled()) {
            return true;
        }else{
            return super.dispatchTouchEvent(e);
        }
    }

    public MFolder getCurrentWidgetFolder() {
        return currentWidgetFolder;
    }

    public void update() {
        if (undoSnackbar != null && undoSnackbar.isShown()) undoSnackbar.dismiss();
    }

    public ActivityHandler getActivityHandler() {
        return handler;
    }

    public void setActivityHandler(final ActivityHandler newHandler) {
        final ActivityHandler currHandler = this.handler;
        update();
        currHandler.pause();
        newHandler.display();
        transition(currHandler, newHandler);
        MainActivity.this.handler = newHandler;
    }

    private void transition(final ActivityHandler currHandler, final ActivityHandler newHandler) {
        newHandler.beginEnterAnimation(new Runnable() {
            @Override
            public void run() {
                currHandler.removeContainer();
                newHandler.setEnabled(true);
            }
        });
        currHandler.beginExitAnimation(new Runnable() {public void run() { }});
    }

    public void setBackButtonVisibile(boolean visibility) {
        ImageView b = getBackButton();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) b.getLayoutParams();

        if (visibility) {
            params.width = (int) getResources().getDimension(R.dimen.main_topbar_button);
            params.height = (int) getResources().getDimension(R.dimen.main_topbar_button);
            b.setVisibility(View.VISIBLE);
        }else{
            params.width = 0;
            params.height = 0;
            b.setVisibility(View.INVISIBLE);
        }
        getHeader().updateViewLayout(b, params);
    }

    public ImageView getBackButton() {
        return findViewById(R.id.back_button);
    }
    public TextView getTitleText() {
        return findViewById(R.id.title);
    }
    public RelativeLayout getHeader() {
        return findViewById(R.id.listHeader);
    }
    public RelativeLayout getMainContainer() {
        return findViewById(R.id.mainContainer);
    }
}
