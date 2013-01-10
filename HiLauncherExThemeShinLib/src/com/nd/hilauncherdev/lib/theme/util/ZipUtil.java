package com.nd.hilauncherdev.lib.theme.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class ZipUtil {
	
	/**
     * TAG
     */
    private final static String TAG = "ZipUtil";
	
	/**
     * sErrorMessage
     */
    public static String sErrorMessage;
    
	 /**
     * 解压zip。
     * 
     * @param sZipPathFile
     *            被解压的zip文件路径。
     * @param sDestPath
     *            解压的目的目录。
     * @return
     * @throws Exception
     */
    public static ArrayList<Object> ectract(String sZipPathFile, String sDestPath, boolean autoRename) {
        // File file = new File("/sdcard");
        ArrayList<Object> allFileName = new ArrayList<Object>();
        try {
            // 先指定压缩档的位置和档名，建立FileInputStream对象
            FileInputStream fins = new FileInputStream(sZipPathFile);
            // 将fins传入ZipInputStream中
            ZipInputStream zins = new ZipInputStream(fins);
            ZipEntry ze = null;
            byte ch[] = new byte[256];
            while ((ze = zins.getNextEntry()) != null) {
                String name = ze.getName();
                name = name.replace('\\', '/');
                if (autoRename) {
                    name = SUtil.renameRes(name);
                }
                File zfile = new File(sDestPath + name);
                File fpath = new File(zfile.getParentFile().getPath());
                if (ze.isDirectory()) {
                    if (!zfile.exists())
                        zfile.mkdirs();
                    zins.closeEntry();
                } else {
                    if (!fpath.exists())
                        fpath.mkdirs();
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    allFileName.add(zfile.getAbsolutePath());
                    while ((i = zins.read(ch)) != -1)
                        fouts.write(ch, 0, i);
                    zins.closeEntry();
                    fouts.close();
                }
            }
            fins.close();
            zins.close();
            sErrorMessage = "OK";
            return allFileName;
        } catch (Exception e) {
            System.err.println("Extract error:" + e.getMessage());
            sErrorMessage = e.getMessage();
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "OutOfMemoryError:" + sZipPathFile, e);
        }
        return null;
    }

    /**
     * 解压文件
     * @param am
     * @param assetPath
     * @param sDestPath
     * @param autoRename
     * @return
     */
    public static boolean ectract(AssetManager am, String assetPath, String sDestPath, boolean autoRename) {
        try {
            InputStream is = am.open(assetPath);
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry ze = null;
            byte ch[] = new byte[256];
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                name = name.replace('\\', '/');
                if (autoRename) {
                    name = SUtil.renameRes(name);
                }
                File zfile = new File(sDestPath + name);
                File fpath = new File(zfile.getParentFile().getPath());
                if (ze.isDirectory()) {
                    if (!zfile.exists())
                        zfile.mkdirs();
                    zis.closeEntry();
                } else {
                    if (!fpath.exists())
                        fpath.mkdirs();
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    while ((i = zis.read(ch)) != -1)
                        fouts.write(ch, 0, i);
                    zis.closeEntry();
                    fouts.close();
                }
            }
            is.close();
            zis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "OutOfMemoryError:" + assetPath, e);
        }
        return false;
    }

    /**
     * 解压文件
     * @param ctx
     * @param assetPath
     * @param sDestPath
     * @param autoRename
     * @return
     */
    public static boolean ectract(Context ctx, String assetPath, String sDestPath, boolean autoRename) {
        try {
            InputStream is = ctx.getAssets().open(assetPath);
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry ze = null;
            byte ch[] = new byte[256];
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                name = name.replace('\\', '/');
                if (autoRename) {
                    name = SUtil.renameRes(name);
                }
                File zfile = new File(sDestPath + name);
                File fpath = new File(zfile.getParentFile().getPath());
                if (ze.isDirectory()) {
                    if (!zfile.exists())
                        zfile.mkdirs();
                    zis.closeEntry();
                } else {
                    if (!fpath.exists())
                        fpath.mkdirs();
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    while ((i = zis.read(ch)) != -1)
                        fouts.write(ch, 0, i);
                    zis.closeEntry();
                    fouts.close();
                }
            }
            is.close();
            zis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Log.w(TAG, "OutOfMemoryError:" + assetPath, e);
        }
        return false;
    }
}
