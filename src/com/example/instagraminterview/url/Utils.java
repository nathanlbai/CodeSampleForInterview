package com.example.instagraminterview.url;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

public class Utils {
	public static ProgressDialog newProgressDialog(Context context){
		return ProgressDialog.show(context,"Please Wait" , "Loading...",true,false);
	}

	public static int getBitmapSize(Bitmap bitmap){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
			return bitmap.getRowBytes()*bitmap.getHeight();
		}else{
			return bitmap.getByteCount();
		}
	}

}
