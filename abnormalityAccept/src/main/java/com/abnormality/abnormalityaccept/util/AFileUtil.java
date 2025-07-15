package com.abnormality.abnormalityaccept.util;

import com.abnormality.abnormalityaccept.exception.ServiceException;

import java.io.File;
import java.io.FileOutputStream;

public class AFileUtil {

    public static boolean writeFile(byte[] bytes, String path){
        //若没有文件夹则创建文件夹
        createDirByFile(path);

        FileOutputStream fos=null;
        try{
            fos=new FileOutputStream(path);
            fos.write(bytes);
            return true;
        } catch (Exception e) {
            throw new ServiceException("文件IO错误"+e.getMessage());
        }
    }

    public static boolean createDirByFile(String filepath){
        String filename=filepath.substring(filepath.lastIndexOf("/")+1);
        String dir=filepath.substring(0,filepath.lastIndexOf("/"));
        return createDir(dir);
    }

    public static boolean createDir(String dirpath){
        File file=new File(dirpath);
        if(!file.exists()){
            file.mkdirs();
        }
        return true;
    }

    public static String createTempFile(String filename, byte[] bytes){
        String path="./temp/"+filename;
        if(writeFile(bytes,path)){
            return path;
        }
        return "";
    }
}
