package com.eliaschenker.recipegenerator.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * @author Elia Schenker
 * 08.07.2022
 * Source: https://stackoverflow.com/questions/14332296/how-to-set-image-from-url-using-asynctask
 * The SetImageToURLTask takes an ImageView, downloads an Image by a url and sets the imageview to it.
 */
public class SetImageToURLTask extends AsyncTask<String, Void, Bitmap> {
    private final ImageView bmImage;

    public SetImageToURLTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
