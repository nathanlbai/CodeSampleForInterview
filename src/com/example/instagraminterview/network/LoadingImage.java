package com.example.instagraminterview.network;

import java.net.URL;

import com.example.instagraminterview.url.AccessPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class LoadingImage extends AsyncTask<String, Void, Bitmap> {

	private final static String LOG_TAG = "LoadImageTask";

	private ImageView mImageView;

	private LoadingImageCallback callback;

	private LruCache<String, Bitmap> mMemoryCache;

	private String imgUrl;

	public LoadingImage(ImageView imageView,
			LruCache<String, Bitmap> mMemoryCache) {

		this.mImageView = imageView;
		this.imgUrl = (String) imageView.getTag();

		this.mMemoryCache = mMemoryCache;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		URL url = null;
		Bitmap bmp = null;
		int count=0;

		try {
			if ((bmp = mMemoryCache.get(params[0])) == null) {
				url = new URL(params[0]);
				bmp = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());

				if(count == 0||count == 3){
					int width = 600;
					int height = 600;
					bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
					count = 0;
				}else{
					int width = 300;
					int height = 300;
					bmp = Bitmap.createScaledBitmap(bmp, width, height, true);					
				}
				count++;
				
				mMemoryCache.put(params[0], bmp);
			}
		} catch (Exception e) {
			if (AccessPoint.DEBUG) {
				e.printStackTrace();
			}
		}
		return bmp;
	}

	@Override
	public void onPostExecute(Bitmap result) {
		try {
			if (this.imgUrl.equalsIgnoreCase((String) this.mImageView.getTag())) {
				this.mImageView.setImageBitmap(result);
			} else {
				if (AccessPoint.DEBUG) {
					Log.d(LOG_TAG, "Skipped");
				}
			}
		} catch (Exception ex) {
		}

		if (callback != null) {
			this.callback.loadingImageCallback();
		}
	}

	public void setCallback(LoadingImageCallback callback) {
		this.callback = callback;
	}

}
