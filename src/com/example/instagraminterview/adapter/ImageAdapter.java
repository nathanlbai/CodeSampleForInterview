package com.example.instagraminterview.adapter;

import java.util.List;
import com.example.instagraminterview.R;
import com.example.instagraminterview.network.LoadingImage;
import com.example.instagraminterview.network.LoadingImageCallback;
import com.example.instagraminterview.url.AccessPoint;
import com.example.instagraminterview.url.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageAdapter extends ArrayAdapter<String> {
	private static final String LOG_TAG = "ImageAdapter";

	private List<String> imageUrls;
	private LruCache<String, Bitmap> mMemoryCache;

	public ImageAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);

		this.imageUrls = objects;

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return Utils.getBitmapSize(bitmap) / 1024;
			}
		};
	}

	public int getCount() {
		return this.imageUrls.size();
	}

	public String getItem(int location) {
		return this.imageUrls.get(location);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_items, parent, false);

			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.image);
			holder.progress = (ProgressBar) row.findViewById(R.id.progress);

			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		String url = this.imageUrls.get(position);
		loadBitmap(url, holder);

		return row;
	}

	// Memory Cache
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void loadBitmap(String imageUrl, ViewHolder holder) {
		Bitmap bitmap = getBitmapFromMemCache(imageUrl);
		holder.image.setTag(imageUrl);

		if (bitmap != null) {
			holder.image.setImageBitmap(bitmap);
			holder.progress.setVisibility(View.GONE);

			if (AccessPoint.DEBUG) {
				Log.v(LOG_TAG, "Loading from cache");
			}

		} else {
			holder.progress.setVisibility(View.VISIBLE);

			LoadingImage loadingImage = new LoadingImage(holder.image,
					mMemoryCache);
			loadingImage.setCallback(holder);
			loadingImage.execute(imageUrl);

			if (AccessPoint.DEBUG) {
				Log.v(LOG_TAG, "Loading from internet");
			}
		}
	}

	private class ViewHolder implements LoadingImageCallback {
		public ImageView image;
		public ProgressBar progress;

		@Override
		public void loadingImageCallback() {
			// TODO Auto-generated method stub
			progress.setVisibility(View.GONE);
		}
	}

}
