package com.myapps.linkwidget.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.URLUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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

    public static Uri parse(String url) {
        return Uri.parse(format(url));
    }

    private static String format(String url) {
        if (!URLUtil.isValidUrl(url)) {
            url = "https://" + url;
        }

        return url;
    }

    public static Bitmap getURLIcon(String uri) {
        String url = format(uri);
        try
        {
            url = "https://" + new URL(url).getHost();

            InputStream input = (InputStream) new URL(url + "/favicon.ico").getContent();
            Bitmap bm = BitmapFactory.decodeStream(input);

            if (bm == null) {
                throw new Exception();
            }else{
                return bm;
            }
        } catch (Exception e) {
            try {
                final String search = "shortcut icon";

                URL urlObject = new URL(url);
                URLConnection con = urlObject.openConnection();
                InputStream stream = con.getInputStream();
                String html = getHtml(stream);

                int searchIndex = html.indexOf("href=\"", html.indexOf(search) + search.length()) + 6;

                stream.close();
                String faviconURL = html.substring(searchIndex, html.indexOf("\"", searchIndex));

                if (faviconURL.startsWith("/")) {
                    faviconURL = "https://" + con.getURL().getHost() + faviconURL;
                }

                InputStream input = (InputStream) new java.net.URL(faviconURL).getContent();
                return BitmapFactory.decodeStream(input);
            }catch (Exception e2) {
                return null;
            }
        }
    }

    private static String getHtml(InputStream inputStream) throws IOException
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }catch (Exception e) {
            throw new IOException();
        }
    }
}