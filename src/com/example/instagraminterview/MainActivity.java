package com.example.instagraminterview;

import java.util.ArrayList;

import com.example.instagraminterview.R;

import com.example.instagraminterview.adapter.ImageAdapter;
import com.example.instagraminterview.network.GetImageUrlsTask;
import com.example.instagraminterview.network.GetUrlsListener;

import android.app.Activity;
import android.os.Bundle;

import android.widget.ListView;

public class MainActivity extends Activity implements GetUrlsListener {

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mListView = (ListView) findViewById(R.id.listView1);

		new GetImageUrlsTask(this, this).execute();
	}

	@Override
	public void imageUrlsReceived(ArrayList<String> result) {
		ImageAdapter mAdapter = new ImageAdapter(this, 0, result);
		this.mListView.setAdapter(mAdapter);
	}

}
