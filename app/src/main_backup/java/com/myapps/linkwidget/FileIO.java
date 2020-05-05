package com.myapps.linkwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class FileIO {
    private static final String URL = "URL";

    public static ArrayList<Url> loadUrlsFromSave(Context c) {
        File path = new File(getDirectory(c), "urls.ser");

        if (!path.exists())
            return new ArrayList<>();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (ArrayList<Url>) in.readObject();
        } catch (Exception ex) {
            saveUrls(new ArrayList<Url>(),c);

            ex.printStackTrace();
            return null;
//            throw new IllegalStateException("Could not deserialize " + path
//                    + ". Probably need to delete serialized files.");

        }
    }

    public static void saveUrls(ArrayList<Url> urls, Context c) {
        File path = new File(getDirectory(c), "urls.ser");

        try {
            FileOutputStream userFile = new FileOutputStream(path);
            ObjectOutputStream userOut = new ObjectOutputStream(userFile);
            userOut.writeObject(urls);
            userOut.close();
            userFile.close();
        }catch (Exception e) { }
    }

    public static void prepareBitmapdirectory(Context c) {
        for (File f : getDirectory(c).listFiles()) {
            if (f.getName().contains("png")) f.delete();
        }
    }

    public static void saveBitmaps(ArrayList<Bitmap> bmps, Context c) {
        for (Bitmap b : bmps) {
            File path = new File(getDirectory(c), b + ".png");
            try {
                FileOutputStream out = new FileOutputStream(path);
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) { }
        }
    }

    public static Bitmap loadBitmapsForIndex(Context c, String name) {
        File path = new File(getDirectory(c), name + ".png");

        if (!path.exists()) {
            return ((BitmapDrawable) c.getResources().getDrawable(R.drawable.internet)).getBitmap();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath(), options);

        return bitmap;
    }

    private static File getDirectory(Context c) {
        return c.getFilesDir();
//        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Widget");
    }
}
