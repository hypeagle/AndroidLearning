package com.hypeagle.common.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/1/12.
 */

public class FileUtils {
    public static boolean isExistFile(String filePath) {
        File file = new File(filePath);
        return isExistFile(file);
    }

    public static boolean isExistFile(File file) {
        return file.exists() && file.isFile() && file.length() > 0;
    }

    public static boolean mkdirs(String path) {
        File file = new File(path);
        return mkdirs(file);
    }

    public static boolean mkdirs(File file) {
        return file.mkdirs();
    }

    public static byte[] readFile(String filePath) {
        File file = new File(filePath);
        return readFile(file);
    }

    public static byte[] readFile(File file) {
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        if (file.exists() && file.isFile()) {
            int fileSize = (int) file.length();

            try {
                fileInputStream = new FileInputStream(file);
                dataInputStream = new DataInputStream(fileInputStream);

                byte[] fileContent = new byte[fileSize];
                if (dataInputStream.read(fileContent, 0, fileSize) == -1) {
                    return null;
                }

                return fileContent;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                        dataInputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        dataInputStream = null;
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                        fileInputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileInputStream = null;
                    }
                }
            }
        }

        return null;
    }


    public static boolean writeFile(String filePath, byte[] fileContent, int offset, boolean forceUpdate) {
        File file = new File(filePath);
        return writeFile(file, fileContent, offset, forceUpdate);
    }

    public static boolean writeFile(File file, byte[] fileContent, int offset, boolean forceUpdate) {
        FileOutputStream fileOutputStream = null;
        DataOutputStream dataOutputStream = null;
        if (!file.exists() || forceUpdate) {
            if (file.exists() && !file.isFile()) {
                return false;
            }

            try {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        return false;
                    }
                }

                fileOutputStream = new FileOutputStream(file);
                dataOutputStream = new DataOutputStream(fileOutputStream);

                dataOutputStream.write(fileContent, offset, fileContent.length);

                return true;
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                        dataOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        dataOutputStream = null;
                    }
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileOutputStream = null;
                    }
                }
            }
        }

        return true;
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);

        return file.delete();
    }
}
