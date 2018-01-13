package com.hypeagle.compression;

import android.util.Base64;
import android.util.Log;

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
    private static final String TAG = "[---HYP---]";

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

    public static byte[] compressForLZMA(byte[] sourceData) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(sourceData);

            com.hypeagle.compression.Compression.LZMA.Encoder encoder = new com.hypeagle.compression.Compression.LZMA.Encoder();
            if (!encoder.SetAlgorithm(2)) {
                Log.d(TAG, "Incorrect compression mode.");
            }
            if (!encoder.SetDictionarySize(1 << 23)) {
                Log.d(TAG, "Incorrect dictionary size.");
            }
            if (!encoder.SetNumFastBytes(128)) {
                Log.d(TAG, "Incorrect FB value.");
            }
            if (!encoder.SetMatchFinder(1)) {
                Log.d(TAG, "Incorrect MF value.");
            }
            if (!encoder.SetLcLpPb(3, 0, 2)) {
                Log.d(TAG, "Incorrect lc or lp or pb value.");
            }
            encoder.SetEndMarkerMode(false);
            encoder.WriteCoderProperties(byteArrayOutputStream);

            int fileSize = sourceData.length;
            for (int i = 0; i < 8; i++)
                byteArrayOutputStream.write((int) (fileSize >>> (8 * i)) & 0xFF);
            encoder.Code(byteArrayInputStream, byteArrayOutputStream, -1, -1, null);

            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            byteArrayInputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] compressForLZMA(String sourceData) {
        byte[] bytes = sourceData.getBytes();
        return compressForLZMA(bytes);
    }

    public static byte[] decompressForLZMA(byte[] compressionData) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(compressionData);

            int propertiesSize = 5;
            byte[] properties = new byte[propertiesSize];
            if (byteArrayInputStream.read(properties, 0, propertiesSize) != propertiesSize) {
                Log.d(TAG, "input .lzma file is too short.");
            }

            com.hypeagle.compression.Compression.LZMA.Decoder decoder = new com.hypeagle.compression.Compression.LZMA.Decoder();
            if (!decoder.SetDecoderProperties(properties)) {
                Log.d(TAG, "Incorrect stream properties.");
            }

            long outSize = 0;
            for (int i = 0; i < 8; i++) {
                int v = byteArrayInputStream.read();
                if (v < 0)
                    throw new Exception("Can't read stream size.");
                outSize |= ((long) v) << (8 * i);
            }
            if (!decoder.Code(byteArrayInputStream, byteArrayOutputStream, outSize))
                throw new Exception("Error in data stream.");

            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            byteArrayInputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decompressForLZMA(String compressionData) {
        byte[] bytes = Base64.decode(compressionData, Base64.DEFAULT);
        return decompressForLZMA(bytes);
    }
}