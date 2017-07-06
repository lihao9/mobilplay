package com.lihao.mobilplay.tools;

import android.os.Environment;

import java.io.File;

/**
 * Created by hbm on 2017/6/24.
 * 作者：李浩
 * 时间：2017/6/24
 * 类的作用：xxxxxx
 */

public class FileUtils {

    public static final String PATH_PHOTOGRAPH = "/LXT/";

    //获取图片文件
    public static File getDCIMFile(String filePath, String imageName) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) { // 文件可用
            File dirs = new File(Environment.getExternalStorageDirectory(),
                    "DCIM"+filePath);
            if (!dirs.exists())
                dirs.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory(),
                    "DCIM"+filePath+imageName);
            if (!file.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    file.createNewFile();
                } catch (Exception e) {
                }
            }
            return file;
        } else {
            return null;
        }

    }
}
