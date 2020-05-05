package com.myapps.linkwidget.mainui;

import android.graphics.Color;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.myapps.linkwidget.model.MFolder;

public abstract class ActivityHandler {
    protected MainActivity c;
    protected static final int ANIM_DUR = 200;
    private boolean enabled = false;
    protected RelativeLayout container;

    public ActivityHandler(MainActivity c, int resource) {
        this.c = c;

        container = (RelativeLayout) c.getLayoutInflater().inflate(resource, null);
    }

    public void pause() {
        setEnabled(false);
    }

    public void removeContainer() {
        c.getMainContainer().removeView(container);
    }

    public abstract boolean backPressed();
    public void display() {
        c.getMainContainer().addView(container, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }


    public abstract void beginEnterAnimation(final Runnable end);
    public abstract void beginExitAnimation(final Runnable end);
}
