package com.rockchip.itvbox;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHelper;

import com.rockchip.itvbox.bridge.Cocos2dxBridge;
import com.rockchip.itvbox.utils.StringUtils;
import com.rockchip.itvbox.utils.SystemSettingUtils;
import com.rockchip.itvbox.utils.WindowHelper;
import com.rockchip.itvbox.view.MainTopView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TVBoxActivity extends Cocos2dxActivity  {
	
	public static final String APP_LAUNCH_ACTION = "com.rockchip.itvbox.APP_LAUNCH_ACTION";
	public static final String EXTRA_PACKAGE = "package";
	private static final String TAG = "Cocos2dxActivity";
	private LayoutInflater mInflater;
	private MainTopView mTopView;
	
    protected void onCreate(Bundle savedInstanceState){
    	mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		super.onCreate(savedInstanceState);	
		mTopView.start();
		Cocos2dxBridge.getInstance().init(this, this);
		Cocos2dxBridge.getInstance().onCreate();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(APP_LAUNCH_ACTION);
		intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mLaunchReceiver, intentFilter);		
	}
    
    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
        super.onConfigurationChanged(newConfig);

        Log.i(TAG,"onConfigurationChanged"); 
        if(newConfig.navigation == Configuration.NAVIGATION_DPAD){
        	Log.i(TAG,"onConfigurationChanged newConfig.navigation=Configuration.NAVIGATION_DPAD"); 
        }else{
        	Log.i(TAG,"onConfigurationChanged newConfig.navigation="+newConfig.navigation); 
        }
    } 
    
    /**
     * ����Activity����,ͳ��Activity��������
     */
    private BroadcastReceiver mLaunchReceiver =  new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			if(Cocos2dxBridge.APP_LAUNCH_ACTION.equals(intent.getAction())){
				String pkg = intent.getStringExtra(Cocos2dxBridge.EXTRA_PACKAGE);
	        	if(!StringUtils.isEmptyObj(pkg)){
	        		SystemSettingUtils.increaseAppLaunch(TVBoxActivity.this, pkg);
	        	}
			}else if(Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())){
				android.os.Process.killProcess(android.os.Process.myPid()); 
			}else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
				NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if(networkInfo!=null&&networkInfo.isConnected()){
					Cocos2dxHelper.setBoolForKey("networkChangeToConnect", true);
					Cocos2dxHelper.setBoolForKey("networkChangeToConnect4tv", true);
					Cocos2dxBridge.getInstance().refreshRecommend();
				}else{
					Cocos2dxHelper.setBoolForKey("networkChangeToConnect", false);
					Cocos2dxHelper.setBoolForKey("networkChangeToConnect4tv", false);
				}
			}
		}
    };
    
    public Cocos2dxGLSurfaceView onCreateView() {
    	Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
    	// RKTVBoxHD should create stencil buffer
    	//glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
    	glSurfaceView.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
    	//glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);//���ó�͸��
    	return glSurfaceView;
    }
    
    @Override
    public View onAddExtensionView() {
    	mTopView = (MainTopView)mInflater.inflate(R.layout.view_main_top, null);
    	LinearLayout.LayoutParams topViewParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                           ViewGroup.LayoutParams.WRAP_CONTENT);
    	mTopView.setLayoutParams(topViewParams);

    	return mTopView;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mTopView.resume();
    	WindowHelper.setFullScreen(getWindow());
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mTopView.pause();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mTopView.stop();
    	Cocos2dxBridge.getInstance().onDestroy();
    	unregisterReceiver(mLaunchReceiver);
    }
    
	static {
		System.loadLibrary("itvbox");
	}
}
