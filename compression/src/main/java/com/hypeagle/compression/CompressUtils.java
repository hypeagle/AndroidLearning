package com.hypeagle.compression;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2018/1/12.
 */

public class CompressUtils {
    public static byte[] compressForGzip(byte[] soureData) {
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(soureData);
            gzipOutputStream.close();

            byte[] compressionData = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

            return compressionData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] compressForGzip(String sourceData) {
        byte[] bytes = sourceData.getBytes();
        return compressForGzip(bytes);
    }

    public static byte[] decompressForGzip(byte[] compressionData) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(compressionData);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

            byte[] bytes = new byte[1024];
            int i;
            while ((i = gzipInputStream.read(bytes, 0, 1024)) > 0) {
                byteArrayOutputStream.write(bytes, 0, i);
            }

            gzipInputStream.close();
            byteArrayInputStream.close();
            byteArrayOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decompressForGzip(String compressionData) {
        byte[] bytes = Base64.decode(compressionData, Base64.DEFAULT);
        return decompressForGzip(bytes);
    }

    public static byte[] compressForZip(byte[] sourceData) {
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry("0"));
            zipOutputStream.write(sourceData);
            zipOutputStream.closeEntry();

            byte[] compressionData = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

            return compressionData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] compressForZip(String sourceData) {
        byte[] bytes = sourceData.getBytes();
        return compressForZip(bytes);
    }

    public static byte[] decompressForZip(byte[] compressionData) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(compressionData);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
            zipInputStream.getNextEntry();

            byte[] bytes = new byte[1024];
            int i;
            while ((i = zipInputStream.read(bytes, 0, 1024)) > 0) {
                byteArrayOutputStream.write(bytes, 0, i);
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
            byteArrayInputStream.close();
            byteArrayOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decompressForZip(String compressionData) {
        byte[] bytes = Base64.decode(compressionData, Base64.DEFAULT);
        return decompressForZip(bytes);
    }
}