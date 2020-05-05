package com.myapps.linkwidget.model;

import com.myapps.linkwidget.serialize.Storage;

import java.io.Serializable;
import java.util.UUID;

public class MStorable implements Serializable {
    private String name;
    private MFolder parent;
    protected boolean deleted = true;
    private String id;

    public MStorable(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(MFolder f) {
        deleted = false;
        parent = f;
    }

    public void delete() {
        getParent().removeStorable(this);
        deleted = true;
    }

    public boolean isDeleted() {
        return getParent().isDeleted() || deleted;
    }

    public MFolder getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
         return (o instanceof MStorable && ((MStorable)o).id.equals(id));
    }

    public String toString() {
//        return id + " " + super.toString();
        return id ;
    }
}
