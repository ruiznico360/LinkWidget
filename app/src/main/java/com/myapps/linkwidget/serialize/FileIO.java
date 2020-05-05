package com.myapps.linkwidget.serialize;

import android.content.Context;

import com.myapps.linkwidget.R;
import com.myapps.linkwidget.model.MFolder;
import com.myapps.linkwidget.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileIO {
    private static final String BASE_FOLDER_SERIALIZE_LOC = "storage.ser";

    public static Storage getStorage(Context c) {
        File path = new File(getDirectory(c), BASE_FOLDER_SERIALIZE_LOC);

        if (!path.exists())
            return new Storage(MFolder.generateBase(c));
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (Storage) in.readObject();
        } catch (Exception ex) {
            path.delete();
            Util.log("Outdated " + BASE_FOLDER_SERIALIZE_LOC + ", recreating...");
            return getStorage(c);
        }
    }
    public static void saveStorage(Storage base, Context c) {
        serialize(c, base, BASE_FOLDER_SERIALIZE_LOC);
    }


    private static File getDirectory(Context c) {
        return c.getFilesDir();
    }

    private static void serialize(Context c, Object o, String fileName) {
        File path = new File(getDirectory(c), fileName);

        try {
            FileOutputStream userFile = new FileOutputStream(path);
            ObjectOutputStream userOut = new ObjectOutputStream(userFile);
            userOut.writeObject(o);
            userOut.close();
            userFile.close();
        }catch (Exception e) {
            Util.log("Error saving to " + fileName);
        }
    }
}
