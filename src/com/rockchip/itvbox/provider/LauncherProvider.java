/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月30日 上午10:20:07  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月30日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.rockchip.itvbox.cache.IImageCache;
import com.rockchip.itvbox.cache.impl.AppIconCache;
import com.rockchip.itvbox.filter.IAppsFilter;
import com.rockchip.itvbox.filter.impl.AppsFilterImpl;
import com.rockchip.itvbox.model.PackageInformation;
import com.rockchip.itvbox.utils.SystemSettingUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.util.Log;

public class LauncherProvider extends BroadcastReceiver {
	public static final String TAG = "LauncherProvider";
	private Context mContext;
	private IImageCache<String> imageCache;
	private LoadAppsCallback mLoadAppsCallback;
	private IAppsFilter mAppsFilter;
	private List<PackageInformation> mPkgList;
	
	public LauncherProvider(Context context){
		mContext = context;
		imageCache = new AppIconCache();
		mAppsFilter = new AppsFilterImpl();
		mPkgList = new ArrayList<PackageInformation>();
	}
	
	public void loadLauncherApps(int cnt){
		new LoaderTask().execute(cnt);
	}
	
	public void setLoadCallback(LoadAppsCallback loadCallback){
		mLoadAppsCallback = loadCallback;
	}

	private JSONArray loadLauncherAppsJson(int total) throws JSONException{
		Log.i(TAG, Log.getStackTraceString(new Throwable()));
		Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

		PackageManager pm = mContext.getPackageManager();
		List<ResolveInfo> resolveList = pm.queryIntentActivities(intent, 0);
		Map<String, Integer> launchMap = SystemSettingUtils.getAppLaunchs(mContext);
		Collections.sort(resolveList, new AppsFilterImpl.AppsComparator(pm, launchMap));

		JSONArray jsonArray = new JSONArray();
		mPkgList.clear();
		for(int i = 0, cnt=0; cnt<total&&i<resolveList.size(); i++){
			ResolveInfo resInfo = resolveList.get(i);
			String pkg = resInfo.activityInfo.packageName;
			if(!mAppsFilter.accept(pkg)){
				continue;
			}
			PackageInformation apkInfo = new PackageInformation();
			apkInfo.setAppIcon(resInfo.loadIcon(pm));
			String appName = resInfo.loadLabel(pm).toString();
			apkInfo.setAppName(appName);
			apkInfo.setActivity(resInfo.activityInfo.name);
			apkInfo.setPkgName(pkg);
			//以activity包路径作为缓存图片名称
			imageCache.putDrawable(apkInfo.getActivity(), apkInfo.getAppIcon());
			Log.e(TAG, "load app -->activity_name="+apkInfo.getActivity());
			jsonArray.put(apkInfo.toJson());
			mPkgList.add(apkInfo);
			cnt++;
		}
		clearImageCacheIfNeed();
		return jsonArray;
	}
	
	/**
	 * 根据需要清理App Icon缓存
	 */
	private void clearImageCacheIfNeed(){
		Log.i(TAG, "clearImageCacheIfNeed:" + Log.getStackTraceString(new Throwable()));
		File cacheDir = new File(imageCache.getRealCacheDirectory());
		if(!cacheDir.exists()) return;
		
		File cacheFiles[] = cacheDir.listFiles();
		for(File file : cacheFiles){
			String fileName = file.getName();
			if(fileName==null) continue;
			String activity = fileName.replace("&", ".");
			if(findPackageInfoByActivity(activity)==null){
				Log.e(TAG, "clearImageCacheIfNeed activity_name="+activity);
				imageCache.remove(activity);
			}
		}
	}
	
	/**
	 * 异步加载Apps
	 */
	private class LoaderTask extends AsyncTask<Integer, Void, String>{
		@Override
		protected String doInBackground(Integer... params) {
			try {
				JSONArray jsonArray = loadLauncherAppsJson(params[0]);
				return jsonArray.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "LoaderTask->result:" + result);
			if(mLoadAppsCallback!=null){
				mLoadAppsCallback.onLoadAppsCompleted(result);
			}
		}
	}
	
	/**
	 * 异步更新apps
	 */
	private class PackageUpdatedTask extends AsyncTask<Integer, Void, String>{

        public static final int OP_NONE = 0;
        public static final int OP_ADD = 1;
        public static final int OP_UPDATE = 2;
        public static final int OP_REMOVE = 3; // uninstlled
        
		@Override
		protected String doInBackground(Integer... params) {
			try {
				JSONArray jsonArray = loadLauncherAppsJson(params[0]);
				return jsonArray.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if(mLoadAppsCallback!=null){
				mLoadAppsCallback.onUpdateAppsCompleted(result);
			}
		}
	}
	
	public interface LoadAppsCallback {
		void onLoadAppsCompleted(String apps);
		void onUpdateAppsCompleted(String apps);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

        if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            final String packageName = intent.getData().getSchemeSpecificPart();
            final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            
            if (packageName == null || packageName.length() == 0) {
                // they sent us a bad intent
                return;
            }
            if(findPackageInfo(packageName)==null){
            	// we don't need to update
            	return;
            }
            
            int op = PackageUpdatedTask.OP_NONE;
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                op = PackageUpdatedTask.OP_UPDATE;
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_REMOVE;
                    SystemSettingUtils.removeAppLaunch(mContext, packageName);
                }
                // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                // later, we will update the package at this time
            } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                if (!replacing) {
                    op = PackageUpdatedTask.OP_ADD;
                } else {
                    op = PackageUpdatedTask.OP_UPDATE;
                }
            }
            if(op==PackageUpdatedTask.OP_UPDATE||op==PackageUpdatedTask.OP_REMOVE){
            	new PackageUpdatedTask().execute(mPkgList.size());
            }
        }
	}
	
	/**
	 * 根据包名查找缓存列表中的包信息
	 * @param pkg 包名
	 * @return
	 */
	private PackageInformation findPackageInfo(String pkg){
		for(PackageInformation pkgInfo : mPkgList){
			if(pkgInfo.getPkgName().equals(pkg)){
				return pkgInfo;
			}
		}
		return null;
	}
	
	/**
	 * 根据Activity名查找缓存列表中的包信息
	 * @param pkg 包名
	 * @return
	 */
	private PackageInformation findPackageInfoByActivity(String activity){
		for(PackageInformation pkgInfo : mPkgList){
			if(pkgInfo.getActivity().equals(activity)){
				return pkgInfo;
			}
		}
		return null;
	}
}
