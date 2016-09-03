/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月30日 上午11:37:03  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月30日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.bridge;

import org.cocos2dx.lib.Cocos2dxHelper.Cocos2dxHelperListener;
import org.json.JSONException;
import org.json.JSONObject;

import com.rockchip.itvbox.TVBoxActivity;
import com.rockchip.itvbox.provider.LauncherProvider;
import com.rockchip.itvbox.provider.LauncherProvider.LoadAppsCallback;
import com.rockchip.itvbox.utils.NetworkDetecting;
import com.rockchip.itvbox.utils.ReflectionUtils;
import com.rockchip.itvbox.utils.SystemSettingUtils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class Cocos2dxBridge implements LoadAppsCallback {

	public static final String APP_LAUNCH_ACTION = "com.rockchip.itvbox.APP_LAUNCH_ACTION";
	public static final String EXTRA_PACKAGE = "package";
	public static final int DEFAULT_APPS_NUM = 8;
	private static final String TAG = "Cocos2dxBridge";
	private Context mContext;
	private static Cocos2dxBridge mCocos2dxBridge;
	private LauncherProvider mLauncherProvider;
	private Cocos2dxHelperListener mCocos2dxHelperListener;
	private NetworkDetecting mNetworkDetecting;
	private MoretvHelper mMoretvHelper;
	private TVStore360Helper mTVStore360Helper;
	private Handler mMainHandler;
	
	private Cocos2dxBridge(){
		mLauncherProvider = null;
		mNetworkDetecting = null;
		mMoretvHelper = null;
		mTVStore360Helper = null;
		mMainHandler = null;
	}

	/**
	 * Context can't be Static
	 * @param context
	 */
	public void init(Context context, Cocos2dxHelperListener listener){
		if(null != context){
		   mContext = context;
		   mLauncherProvider = new LauncherProvider(mContext);
		   mNetworkDetecting = new NetworkDetecting(mContext);
		   mMoretvHelper = new MoretvHelper(mContext);
		   mTVStore360Helper = new TVStore360Helper(mContext);
		   mLauncherProvider.setLoadCallback(this);
		}
		mMainHandler = new Handler();
		mCocos2dxHelperListener = listener;
	}
	
	public void onCreate(){
		if(mContext!=null){
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
			intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
			intentFilter.addDataScheme("package");
			mContext.registerReceiver(mLauncherProvider, intentFilter);
			mMoretvHelper.registerDownloadBroadcast();
			mTVStore360Helper.registerDownloadBroadcast();
		}
		//nativeRemoveAllTextures();
	}
	
	public void onDestroy(){
		if(mContext!=null){
			mContext.unregisterReceiver(mLauncherProvider);
			mMoretvHelper.unregisterDownloadBroadcast();
			mTVStore360Helper.unregisterDownloadBroadcast();
		}
	}
	
	public static Cocos2dxBridge getInstance(){
		if(mCocos2dxBridge == null){
			mCocos2dxBridge = new Cocos2dxBridge();
		}
		return mCocos2dxBridge;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	/**
	 * 启动Moretv
	 */
	public boolean startMoreTVWithPage(String page){
		if(!mNetworkDetecting.detect()){
			return false;
		}
		Intent intent = new Intent();
		intent.setAction("moretv.action.applaunch");
		intent.putExtra("Data", page); 
		intent.putExtra("ReturnMode", 0); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		if(!startActivity(intent)){//未安装moretv
			mMoretvHelper.showDownloadMoreTv();
			return false;
		}
		return true;
	}

	/**
	 * Launch TVStore360
	 */
	public boolean startTVStore360(String pkgName, String activity){
		if(!mNetworkDetecting.detect()){
			return false;
		}
		Intent intent = new Intent();
		intent.setClassName(pkgName, activity);
		if(!mContext.getPackageName().equals(pkgName))
	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		SystemSettingUtils.increaseAppLaunch(mContext, pkgName);
		if(!startActivity(intent)){//未安装moretv
			mTVStore360Helper.showDownloadTVStore360();
			return false;
		}
		return true;
	}

	public static boolean launchTVStore360(final String pkgName, final String activity){
		Log.d(TAG, "Link to startTVStore360: "+activity);
		Runnable install_runnable = new Runnable(){
			public void run() {
				getInstance().startTVStore360(pkgName, activity);
			}
		};
		getInstance().mMainHandler.post(install_runnable);
		return true;
	}

	
	/**
     * 加载Apps图标
     */
    public void loadLauncherApps(){
    	Log.i(TAG, "loadLauncherApps");
        if(null!=mLauncherProvider){
            mLauncherProvider.loadLauncherApps(DEFAULT_APPS_NUM);
        }
    }
    
    /**
     * Apps加载完成
     */
	public void onLoadAppsCompleted(final String apps) {
		Log.i(TAG, "onLoadAppsCompleted->apps:" + apps);
		Runnable action = new Runnable(){
			public void run() {
				nativeLoadAppsCompleted(apps);
			}
		};
		mCocos2dxHelperListener.runOnGLThread(action);
	}
	public void onUpdateAppsCompleted(final String apps) {
		Runnable action = new Runnable(){
			public void run() {
				nativeUpdateAppsCompleted(apps);
			}
		};
		mCocos2dxHelperListener.runOnGLThread(action);
	}
	
	/**
	 * 供JNI调用
	 */
	public static void loadApps(){
		Cocos2dxBridge.getInstance().loadLauncherApps();
	}
	
	/**
	 * 启动Activity
	 * @param pkgName
	 * @param activity
	 */
	public static boolean startActivity(String pkgName, String activity){
		//临时处理
		if(activity!=null){
			if(activity.endsWith("android_4_2.WifiSetting")){
				if(Build.VERSION.SDK_INT==19){
					activity = "com.rk.setting.wifi.android_4_4.WifiSetting";
				}else{
					Intent intent = new Intent();
					intent.setClassName("com.rk.setting", "com.rk.setting.Settings");
					//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					startActivity(intent);
				}
			}else if(activity.endsWith("ScreenSettingActivity")){
				if(Build.VERSION.SDK_INT<19){
					Intent intent = new Intent();
					intent.setClassName("com.rk.setting", "com.rk.setting.Settings");
					//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					startActivity(intent);
				}
			}
		}
		Intent intent = new Intent();
		intent.setClassName(pkgName, activity);
		Context context = Cocos2dxBridge.getInstance().getContext();
		if((null!=context)&&(!context.getPackageName().equals(pkgName))){
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		    SystemSettingUtils.increaseAppLaunch(context, pkgName);
		}
		return startActivity(intent);
	}
	
	/**
	 * 根据提供json串解析启动activity
	 * @param json
	 * @return
	 */
	public static boolean startActivityWithParams(String jsonstr){
		Intent intent = new Intent();
		try {
			JSONObject jsonObj = new JSONObject(jsonstr);
			if(jsonObj.has("package")&&jsonObj.has("activity")){
				intent.setClassName(jsonObj.getString("package"), jsonObj.getString("activity"));
			}
			if(jsonObj.has("action")){
				intent.setAction(jsonObj.getString("action"));
			}
			if(jsonObj.has("uri")){
				intent.setData(Uri.parse(jsonObj.getString("uri")));
			}
			//add...
		} catch (JSONException e) {
			e.printStackTrace();
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		return startActivity(intent);
	}
	
	/**
	 * 启动More TV影视分类
	 * @param page
	 * @return
	 */
	public static boolean startMoTvActivity(final String page){
		Log.d(TAG, "Link to motv page: "+page);
		Runnable moretvAction = new Runnable(){
			public void run() {
				getInstance().startMoreTVWithPage(page);
			}
		};
		getInstance().mMainHandler.post(moretvAction);
		return true;
	}

	private static boolean startActivity(Intent intent){
		try{
			Context context = Cocos2dxBridge.getInstance().getContext();
			if(null!=context){
				context.startActivity(intent);
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 刷新热门推荐
	 */
	public void refreshRecommend(){
		nativeRefreshRecommend();
	}
	
	// ===========================================================
	// 获取Android资源文件 Add by fxw 20140324
	// ===========================================================
	public static String[] getStringArrayForKey(String key) {
		Log.i(TAG, "getStringArrayForKey->key:" + key);
		Object value = ReflectionUtils.getStaticFieldValue("com.rockchip.itvbox.R$array", key);
		if(value!=null){
			try{
				Context context = Cocos2dxBridge.getInstance().getContext();
				if(null!=context){
				  return context.getResources().getStringArray((Integer)value);
				}
			}catch(NotFoundException nfe){
				Log.d(TAG, "Get string array not found, key: "+key);
			}
		}
		/*
		if("topmenu_array".equals(key)){
			String str[] = sContext.getResources().getStringArray(R.array.topmenu_array);
			return str;
		}else if("apps_default_app_array".equals(key)){
			String str[] = sContext.getResources().getStringArray(R.array.apps_default_app_array);
			return str;
		}*/
		return new String[]{};
    }
	
	public static String getStringForKey(String key) {
		Object value = ReflectionUtils.getStaticFieldValue("com.rockchip.itvbox.R$string", key);
		if(value!=null){
			try{
				Context context = Cocos2dxBridge.getInstance().getContext();
				if(null!=context){
				   return context.getResources().getString((Integer)value);
				}
			}catch(NotFoundException nfe){
				Log.e(TAG, "Get string not found, key: "+key);
			}
		}
		return "";
    }
	
	//刷新UI
	public static void refreshGLUI(){
		Context context = Cocos2dxBridge.getInstance().getContext();
		if(null!=context){
		  ((TVBoxActivity)context).refreshGLUI();
		}
		Log.d(TAG, "refreshGLUI from native");
	}
	
	//检测当前网络是否已连接
	public static boolean isNetworkConnected(){
		Context context = Cocos2dxBridge.getInstance().getContext();
		NetworkDetecting mNetworkDetecting = null;
		if(null!=context){
		   mNetworkDetecting = new NetworkDetecting(context);
		   return mNetworkDetecting.isConnected();
		}
		return false;
	}
	
	//==========================JNI Native Method===========================//
	public native void nativeLoadAppsCompleted(String apps);
	public native void nativeUpdateAppsCompleted(String apps);
	public native void nativeRemoveAllTextures();
	public native void nativeRefreshRecommend();
}
