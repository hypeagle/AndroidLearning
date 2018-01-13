package com.hypeagle.androidlearning;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hypeagle.common.utils.FileUtils;
import com.hypeagle.compression.CompressUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "[---HYP---]";

    private static final int TAKE_PHOTO = 1;

    private Uri mImageUri;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("learning");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }

                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mImageUri = Uri.fromFile(outputImage);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {

                File file = new File(mImageUri.getPath());
                byte[] fileContent = FileUtils.readFile(file);
                assert fileContent != null;
                Log.d(TAG, "picture length = " + fileContent.length);
                String encodeFileContent = Base64.encodeToString(fileContent, Base64.DEFAULT);
                Log.d(TAG, "encodeFileContent length = " + encodeFileContent.length());
                byte[] compressData = CompressUtils.compressForLZMA(encodeFileContent);
                assert compressData != null;
                Log.d(TAG, "compressData length = " + compressData.length);
                String encodeCompressData = Base64.encodeToString(compressData, Base64.DEFAULT);
                Log.d(TAG, "encodeCompressData length = " + encodeCompressData.length());

                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
