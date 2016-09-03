/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月26日 下午2:50:15  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月26日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.view;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;

import com.rockchip.itvbox.R;
import com.rockchip.itvbox.model.Weather;
import com.rockchip.itvbox.provider.EthernetDataTracker;
import com.rockchip.itvbox.service.IWeatherService;
import com.rockchip.itvbox.service.impl.ChinaWeatherServiceImpl;
import com.rockchip.itvbox.utils.HttpRequestTask;
import com.rockchip.itvbox.utils.HttpRequestTask.HttpRequestListener;
import com.rockchip.itvbox.utils.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainTopView extends LinearLayout {
	private static final String TAG = "MainTopView";
	//State
	private static final int STATE_TOP_STOPPED = 0;
	private static final int STATE_TOP_STARTED = 1;
	private static final int STATE_TOP_PAUSE = 2;
	
	//Message
	private static final int MSG_UPDATE_TIME = 1;
	private static final int MSG_UPDATE_WEATHER = 2;
	private static final int MSG_UPDATE_WEATHER_UI = 3;
	
	//Update Cycle
	private static final int CYCLE_UPDATE_TIME = 5000;
	private static final int CYCLE_UPDATE_WEATHER = 60*60*1000;//1hour
	
	private WifiManager mWifiManager;
	private ImageView mEthernetLinkView;
	private ImageView mWifiSignalView;
	private TextView mTimeView;
	private ImageView mWeatherIconView;
	private TextView mWeatherTitleView;
	private ImageView mSplitView;
	private Handler mMainHander = null;
	private int mState = STATE_TOP_STOPPED;
	private int mRssi = -999;
	private IWeatherService mWeatherService;
	private String mCityCode;

	boolean mEthernetEnabled;
	boolean mEthernetConnected;
	int mEthernetState = 0;

	public MainTopView(Context context) {
		super(context);
	}

	public MainTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mEthernetLinkView = (ImageView)findViewById(R.id.img_ethernet_link);
		mWifiSignalView = (ImageView)findViewById(R.id.img_wifi_signal);
		mTimeView = (TextView)findViewById(R.id.txt_time);
		mWeatherIconView = (ImageView)findViewById(R.id.img_weather);
		mWeatherTitleView = (TextView)findViewById(R.id.txt_weather);
		mWifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
		mWifiSignalView.setImageResource(R.drawable.main_wifi_signal);
		mSplitView = (ImageView)findViewById(R.id.img_top_split);
	}
	
	//	Wifi广播监听
	private BroadcastReceiver mWifiReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)
					|| WifiManager.RSSI_CHANGED_ACTION.equals(action)
					|| WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
					|| WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
				updateWifiSignal();
			}
		}
	};

	//BroadcastReceiver for Ethernet Link 
	private BroadcastReceiver mEthernetReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(EthernetDataTracker.ETHERNET_STATE_CHANGED_ACTION)){
				mEthernetState = intent.getIntExtra(EthernetDataTracker.EXTRA_ETHERNET_STATE, 0);
				Log.d(TAG, "     ETHERNET_STATE_CHANGED_ACTION,BroadcastReceiver state=" + mEthernetState);
			}else if(action.equals(EthernetDataTracker.ETHERNET_IFACE_STATE_CHANGED_ACTION)){
	    	    //mEthernetState=intent.getIntExtra(EthernetDataTracker.EXTRA_ETHERNET_IFACE_STATE, 0);
	    	    Log.d(TAG, "ETHERNET_IFACE_STATE_CHANGED_ACTION,BroadcastReceiver state=" + mEthernetState);
	        }
			updateEthernetLink(mEthernetState);
		}
	};

	
	/**
	 * 启动
	 */
	public void start(){
		if(mState == STATE_TOP_STARTED){
			return;
		}
		mMainHander = new UpdateHandler();
		mState = STATE_TOP_STARTED;
		
		IntentFilter intentFilterWifi = new IntentFilter(); //注册WIFI监听
		intentFilterWifi.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilterWifi.addAction(WifiManager.RSSI_CHANGED_ACTION);
		getContext().registerReceiver(mWifiReceiver, intentFilterWifi);

		IntentFilter intentFilterEthernet = new IntentFilter(); //注册Ethernet监听
		intentFilterEthernet.addAction(EthernetDataTracker.ETHERNET_STATE_CHANGED_ACTION);
		intentFilterEthernet.addAction(EthernetDataTracker.ETHERNET_IFACE_STATE_CHANGED_ACTION);
		getContext().registerReceiver(mEthernetReceiver, intentFilterEthernet);
		
		mWeatherService = new ChinaWeatherServiceImpl();//获取天气
		updateTopInfo(2000);
	}
	
	//回到桌面，立即更新
	public void resume(){
		if(mState==STATE_TOP_PAUSE){
			updateTopInfo(1000);
		}
		mState = STATE_TOP_STARTED;
		
	}
	
	//进入后台时，暂停更新
	public void pause(){
		mState = STATE_TOP_PAUSE;
	}
	
	/**
	 * 停止
	 */
	public void stop(){
		if(mState == STATE_TOP_STOPPED){
			return;
		}
		mMainHander.removeCallbacksAndMessages(null);
		//mMainHander = null;
		getContext().unregisterReceiver(mWifiReceiver);
		getContext().unregisterReceiver(mEthernetReceiver);
		mState = STATE_TOP_STOPPED;
	}
	
	/**
	 * 更新顶部信息
	 */
	private void updateTopInfo(int delayTime){
		Runnable updateAction = new Runnable() {
			public void run() {
				updateWifiSignal();
				updateEthernetLink(mEthernetState);				
				updateTime();
				updateWeather();
			}
		};
		mMainHander.postDelayed(updateAction, delayTime);
	}
	
	//更新天气
	private void updateWeather(){
		HttpRequestListener<Weather> listener = new HttpRequestListener<Weather>(){
			@Override
			public Weather onRequest() {
				if(!StringUtils.hasText(mCityCode)){
					mCityCode = mWeatherService.getCityCode(getContext());
				}
				if(!StringUtils.hasText(mCityCode)){
					return null;
				}
				return mWeatherService.getCurrentWeather(mCityCode);
			}

			@Override
			public void onResponse(Weather result) {
				Message msg = mMainHander.obtainMessage();
				msg.what = MSG_UPDATE_WEATHER_UI;
				msg.obj = result;
				msg.sendToTarget();
			}
		};
		new HttpRequestTask<Weather>(listener).execute();
	}
	
	//更新信号强度
	private void updateWifiSignal(){
		if(mState == STATE_TOP_PAUSE) return;
		
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		if(mWifiManager.isWifiEnabled()&&wifiInfo!=null&&wifiInfo.getIpAddress()!=0){
			int rssi = wifiInfo.getRssi();
			int oldLevel = WifiManager.calculateSignalLevel(mRssi, 4);
			int level = WifiManager.calculateSignalLevel(rssi, 4);
			if((mRssi!=-999&&oldLevel==level) || rssi==Integer.MAX_VALUE){
				return;
			}
			mRssi = rssi;
			mWifiSignalView.setVisibility(View.VISIBLE);
			mWifiSignalView.setImageLevel(level);
		}else{
			mWifiSignalView.setVisibility(View.GONE);
		}
	}

    private void updateEthernetLink(int state){
        switch (state) {
			case EthernetDataTracker.ETHER_STATE_CONNECTING:
				mEthernetLinkView.setVisibility(View.GONE);
				break;
            case EthernetDataTracker.ETHER_STATE_CONNECTED:
                mEthernetLinkView.setVisibility(View.VISIBLE);
                break;
            case EthernetDataTracker.ETHER_STATE_DISCONNECTED:
                mEthernetLinkView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
	}
	
	
	//更新时钟
	private void updateTime(){
		mMainHander.sendEmptyMessage(MSG_UPDATE_TIME);
	}
	
	class UpdateHandler extends Handler{
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_UPDATE_TIME:
				if(mState == STATE_TOP_STARTED){
					Calendar calendar = Calendar.getInstance();
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					int minute = calendar.get(Calendar.MINUTE);
					mTimeView.setVisibility(View.VISIBLE);
					mTimeView.setText(padZero(hour)+":"+padZero(minute));//时分
				}
				if(mState==STATE_TOP_STARTED || mState==STATE_TOP_PAUSE){
					mMainHander.sendEmptyMessageDelayed(MSG_UPDATE_TIME, CYCLE_UPDATE_TIME);
				}
				break;
			case MSG_UPDATE_WEATHER:
				updateWeather();
				break;
			case MSG_UPDATE_WEATHER_UI:
				Weather weather = (Weather)msg.obj;
				if(weather!=null){
					//mWeatherTitleView.setVisibility(View.VISIBLE);
					
					try {
						mWeatherTitleView.setText(new String(weather.getWeather().getBytes(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					int iconID = weather.getFromIcon();
					if(iconID>0){
						mSplitView.setVisibility(View.VISIBLE);
						mWeatherIconView.setVisibility(View.VISIBLE);
						mWeatherIconView.setImageResource(iconID);
					}
				}
				
				if(mState==STATE_TOP_STARTED || mState==STATE_TOP_PAUSE){
					mMainHander.sendEmptyMessageDelayed(MSG_UPDATE_WEATHER, CYCLE_UPDATE_WEATHER);
				}
				break;
			}
		}
		
		private String padZero(int value){
			if(value<10) return "0"+value;
			return ""+value;
		}
	}

}
