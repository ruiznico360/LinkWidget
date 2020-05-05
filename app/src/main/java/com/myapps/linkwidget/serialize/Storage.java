package com.myapps.linkwidget.serialize;

import android.util.SparseArray;

import com.myapps.linkwidget.model.MFolder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Storage implements Serializable {
    private final MFolder BASE;
    private Map<Integer, MFolder> widgetCurrents = new HashMap<>();

    public Storage(MFolder base) {
        this.BASE = base;
    }

    public MFolder getBase() {
        return BASE;
    }

    public Map<Integer, MFolder> getWidgetCurrents() {
        return widgetCurrents;
    }

    public void setWidgetCurrents(Map<Integer, MFolder> map) {
        widgetCurrents = map;
    }

    public MFolder getCurrent(int widgetID) {
        return widgetCurrents.get(widgetID);
    }

    public boolean isViewingBase(int widgetID) {
        return BASE.equals(widgetCurrents.get(widgetID));
    }
}
