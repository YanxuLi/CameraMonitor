package com.media.dingping.cameramonitor.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class FilesUtil {

    public static void saveBitmapToFile(Bitmap bitmap, String filePath) {
        if(bitmap != null && filePath != null) {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()) {
                parentFile.mkdir();
            }

            if(file.exists()) {
                file.delete();
            }

            FileOutputStream out = null;

            try {
                out = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
            } catch (FileNotFoundException var16) {
                var16.printStackTrace();
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException var15) {
                        var15.printStackTrace();
                    }

                    out = null;
                }

            }

        }
    }

    public static void saveBytesToFile(byte[] data, String filePath) {
        if(data != null && filePath != null) {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()) {
                parentFile.mkdir();
            }

            if(file.exists()) {
                file.delete();
            }

            FileOutputStream out = null;

            try {
                out = new FileOutputStream(filePath);
                out.write(data, 0, data.length);
                out.flush();
                out.close();
            } catch (FileNotFoundException var16) {
                var16.printStackTrace();
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException var15) {
                        var15.printStackTrace();
                    }

                    out = null;
                }

            }

        }
    }

    public static void deleteFile(File file) {
        if(file != null && file.exists()) {
            if(file.isFile()) {
                file.delete();
            } else {
                if(file.isDirectory()) {
                    File[] childFile = file.listFiles();
                    if(childFile == null || childFile.length == 0) {
                        file.delete();
                        return;
                    }

                    File[] var2 = childFile;
                    int var3 = childFile.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File f = var2[var4];
                        deleteFile(f);
                    }

                    file.delete();
                }

            }
        }
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();

        try {
            InputStream e = am.open(fileName);
            image = BitmapFactory.decodeStream(e);
            e.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return image;
    }

    public static Drawable getDrawableFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        InputStream is = null;

        try {
            is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
            is = null;
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }

                is = null;
            }

        }

        if(image != null) {
            byte[] chunk = image.getNinePatchChunk();
            if(chunk != null) {
                boolean result = NinePatch.isNinePatchChunk(chunk);
                NinePatchDrawable patchy = new NinePatchDrawable(image, chunk, new Rect(), (String)null);
                return patchy;
            } else {
                return new BitmapDrawable(image);
            }
        } else {
            return null;
        }
    }
}
