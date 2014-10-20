package com.example.instagraminterview.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.instagraminterview.url.AccessPoint;
import com.example.instagraminterview.url.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetImageUrlsTask extends AsyncTask<Void, Void, ArrayList<String>> {
	private final String LOG_TAG = "GetImageUrlsTask";

	private ProgressDialog progressDialog;

	private GetUrlsListener listener;
	private Context context;

	public GetImageUrlsTask(GetUrlsListener listener, Context contex) {
		this.listener = listener;
		this.context = contex;

		if (this.listener == null) {
			throw new NullPointerException();
		}
	}

	@Override
	protected void onPreExecute() {
		this.progressDialog = Utils.newProgressDialog(this.context);
	}

	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		HttpGet get = new HttpGet(AccessPoint.URL);

		InputStream inputStream = null;

		ArrayList<String> res = new ArrayList<String>();
		HashSet<String> tempRes = new HashSet<String>(); //needed to remove duplicates

		try {
			if (AccessPoint.DEBUG) {
				Log.d(LOG_TAG, "Start receiving urls");
			}

			HttpResponse response = httpClient.execute(get, httpContext);
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			StringBuffer buf = new StringBuffer();

			int c;
			while ((c = inputStream.read()) != -1) {
				buf.append((char) c);
			}

			JSONObject jsonObj = new JSONObject(buf.toString());

			JSONArray data = jsonObj.getJSONArray("data");

			int dataItemsCount = data.length();
			for (int i = 0; i < dataItemsCount; ++i) {
				JSONObject dataItem = data.getJSONObject(i);

				// parse large photo
				tempRes.add(dataItem.getJSONObject("images").getJSONObject("standard_resolution")
						.getString("url"));

				JSONArray comments = dataItem.getJSONObject("comments")
						.getJSONArray("data");

				int commentsCount = comments.length();
				for (int j = 0; j < commentsCount - 1; ++j) { // parsing photos in
															// comments
					tempRes.add(comments.getJSONObject(i).getJSONObject("from")
							.getString("profile_picture"));
				}
			}

		} catch (Exception e) {
			if (AccessPoint.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (AccessPoint.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
		
		res.addAll(tempRes);

		return res;
	}

	@Override
	public void onPostExecute(ArrayList<String> result) {
		this.progressDialog.dismiss();

		if (this.listener != null && result != null) {
			listener.imageUrlsReceived(result);
		}

		if (AccessPoint.DEBUG) {
			int urlsCount = result.size();

			for (int i = 0; i < urlsCount; ++i) {
				Log.d(this.LOG_TAG,
						String.format("Url %d: %s", i, result.get(i)));
			}
		}
	}

}
