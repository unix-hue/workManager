package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.Constants;

public class BlurWorker extends Worker {

    public BlurWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        WorkerUtils.makeStatusNotification("Blurring image", applicationContext);
        WorkerUtils.sleep();
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("uri", "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            ContentResolver resolver = applicationContext.getContentResolver();
            Bitmap picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)));
            Bitmap output = WorkerUtils.blurBitmap(picture, applicationContext);
            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build();

            return Result.success(outputData);
        } catch (Throwable throwable) {
            Log.e("blur", "Error applying blur", throwable);
            return Result.failure();
        }
    }
}
