package com.myapps.linkwidget.model;

import android.content.Context;

import com.myapps.linkwidget.R;
import com.myapps.linkwidget.serialize.Storage;

import java.util.ArrayList;

public class MFolder extends MStorable {
    private ArrayList<MStorable> storables;

    public MFolder(String name) {
        super(name);
        storables = new ArrayList<>();
    }


    public void addStorable(MStorable s) {
        storables.add(s);
        s.setParent(this);
    }
    public void addStorable(MStorable s, int index) {
        storables.add(index, s);
        s.setParent(this);
    }

    public boolean contains(String folderName) {
        for (MStorable s : storables) {
            if (s.getName().equals(folderName)) return true;
        }
        return false;
    }

    public void removeStorable(int index) {
        storables.remove(index);
    }

    public void removeStorable(MStorable s) {
        removeStorable(storables.indexOf(s));
    }

    public MStorable getStorable(int index) {
        return storables.get(index);
    }

    public int getSize() {
        return storables.size();
    }

    public int indexOf(MStorable s) {
        return storables.indexOf(s);
    }

    public static Base generateBase(Context c) {
        return new Base(c.getResources().getString(R.string.baseFolder));
    }

    private static class Base extends MFolder {
        public Base(String name) {
            super(name);
        }

        public boolean isDeleted() {
            return false;
        }
    }
}
