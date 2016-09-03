/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     Martin.Cheng@rock-chips.com
* Create at:   2014年8月13日 上午14:26:00 
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年8月13日      fxw         1.0         create
*******************************************************************/ 

package com.rockchip.itvbox.bridge;

import java.io.File;

import com.rockchip.itvbox.R;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class TVStore360Helper{

	public static final String TAG = "TVStore360Helper";
	public static final String KEY_TVSTORE360_DOWNLOAD_ID = "TVStore360_DOWNLOAD_ID";
	public static final String KEY_TVSTORE360_DOWNLOAD_LEN = "TVStore360_DOWNLOAD_LEN";
	public static final String TVSTORE360_APK = "360tvstore.apk";
	public static final String TVSTORE360_URL = "http://down.360safe.com/tvzhushou/360tvstore.apk";
	private Context mContext;
	
	public TVStore360Helper(Context context){
		mContext = context;
	}
	
	public void registerDownloadBroadcast(){
		mContext.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));   
	}
	
	public void unregisterDownloadBroadcast(){
		mContext.unregisterReceiver(mDownloadReceiver);
	}
	
	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
				long downCompleteID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
				long downloadID = sp.getLong(KEY_TVSTORE360_DOWNLOAD_ID, -2);
				if(downCompleteID==downloadID){
					File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
					File apkFile = new File(downloadDir, TVSTORE360_APK);
					if(apkFile.exists()&&apkFile.length()>0){
						saveApkFileLength(apkFile.length());
						installTVStore360IfExisted();
					}
					Log.d(TAG, "Download completed. ");
				}
			}
		}
	};
	
	/**
	 * TVStore360 not installed, Prompt to download
	 */
	public void showDownloadTVStore360(){
		//1.  Check TVStore360 Exists;
		if(installTVStore360IfExisted()){
			return;
		}
		
		//2. TVStore360 Not Downloaded
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		long downloadID = sp.getLong(KEY_TVSTORE360_DOWNLOAD_ID, -1);
		final DownloadManager mDownloadManager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
		boolean isDownloading = false;
		if(downloadID>0){
			int status = queryDownloadStatus(mDownloadManager, downloadID);
			if(status==DownloadManager.STATUS_PENDING||status==DownloadManager.STATUS_RUNNING){
				isDownloading = true;
			}else{
				isDownloading = false;
				sp.edit().remove(KEY_TVSTORE360_DOWNLOAD_ID).remove(KEY_TVSTORE360_DOWNLOAD_LEN).commit();
				mDownloadManager.remove(downloadID);
			}
		}
		
		//3.Prompt to Download TVStore360
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(R.string.dialog_prompt);
		if(isDownloading){
			dialogBuilder.setMessage(R.string.tvstore360_msg_downloading);
			dialogBuilder.setPositiveButton(R.string.dialog_ok, null);
		}else{
			dialogBuilder.setMessage(R.string.tvstore360_msg_not_existed);
			dialogBuilder.setPositiveButton(R.string.moretv_download, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse(TVSTORE360_URL);
					DownloadManager.Request downRequest = new DownloadManager.Request(uri);
					downRequest.setShowRunningNotification(true);
					downRequest.setTitle(mContext.getString(R.string.tvstore360));
					//downRequest.setAllowedNetworkTypes(Request.NETWORK_WIFI);
					downRequest.setAllowedOverRoaming(false);
					downRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, TVSTORE360_APK);
					long downID = mDownloadManager.enqueue(downRequest);
					sp.edit().putLong(KEY_TVSTORE360_DOWNLOAD_ID, downID).commit();
					Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
					try{
						mContext.startActivity(intent);
					}catch(Exception e){}
				}
			});
			dialogBuilder.setNegativeButton(R.string.dialog_cancel, null);
		}
		dialogBuilder.create().show();
	}
	
	//Query State of Download
	private int queryDownloadStatus(DownloadManager downloadManager, long downloadID) {   
		DownloadManager.Query query = new DownloadManager.Query();   
		query.setFilterById(downloadID);   
		Cursor cursor = downloadManager.query(query);   
		int status = -1;
		if(cursor.moveToFirst()) {   
			status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));   
		}
		cursor.close();
		return status;
	}
	
	// Install TVStore360
	private boolean installTVStore360IfExisted(){
		File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File apkFile = new File(downloadDir, TVSTORE360_APK);
		if(!apkFile.exists()||apkFile.length()!=getApkFileLength()){
			Log.d(TAG, "TVStore360 is not exist. ");
			return false;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);   
		intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");   
		try{
			mContext.startActivity(intent);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	// Save APK Length
	public void saveApkFileLength(long len){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		sp.edit().putLong(KEY_TVSTORE360_DOWNLOAD_LEN, len).commit();
	}
	
	//Query APK Length
	public long getApkFileLength(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		return sp.getLong(KEY_TVSTORE360_DOWNLOAD_LEN, -1);
	}
}


