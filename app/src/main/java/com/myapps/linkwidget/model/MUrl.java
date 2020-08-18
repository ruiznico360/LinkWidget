package com.myapps.linkwidget.model;


public class MUrl extends MStorable {
    private String url;

    public MUrl(String name, String url) {
        super(name);
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}