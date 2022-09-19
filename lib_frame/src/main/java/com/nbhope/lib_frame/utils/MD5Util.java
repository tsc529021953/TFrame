package com.nbhope.lib_frame.utils;

import timber.log.Timber;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by ywr on 2021/11/12 10:30
 */
public class MD5Util {
    static String TAG = "MD5";

    public static String getFileMD5(String filepath) {
        return getFileMD5(new File(filepath));
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(e);
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static boolean checkMd5(String Md5, String file) {
        if (Md5 == null) return false;
        String str = getFileMD5(new File(file));
        Timber.d("md5sum = " + str);
        // if(Md5.compareTo(str) == 0) return true;
        if (Md5.compareToIgnoreCase(str) == 0) {
            Timber.d("compareToIgnoreCase OK");
            return true;
        } else {
            Timber.d("compareToIgnoreCase Fail");
            return false;
        }

    }
}
