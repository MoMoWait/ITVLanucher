/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    NetworkDetecting.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-9-9 下午06:02:42  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2011-9-9      xwf         1.0         create
*******************************************************************/   


package com.rockchip.itvbox.utils;


import com.rockchip.itvbox.R;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 网络检测
 */
public class NetworkDetecting {
	
	private Context mContext;
	private boolean isConnected;
	private AlertDialog mAlertDialog;
	
	public NetworkDetecting(Context context){
		mContext = context;
	}
	
	/**
	 * 检测当前网络状态
	 * 已连接true 未连接false
	 */
	public boolean detect(){
		isConnected = isConnect();
		if(isConnected) {
			if(mAlertDialog!=null){
				mAlertDialog.dismiss();
			}
			return isConnected;
		}
		showAlertDialog();
		return isConnected;
	}
	
	/**
	 * 弹出窗口提示进行网络设置
	 */
	private void showAlertDialog(){
		if(mAlertDialog == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.dialog_prompt);
			builder.setMessage(R.string.dialog_network_connect);
			builder.setNegativeButton(mContext.getString(R.string.dialog_cancel), null);
			mAlertDialog = builder.create();
		}
		mAlertDialog.show();
	}
	
	
	/**
	 * 判断当前网络是否已连接或正在连接
	 * @return
	 */
	private boolean isConnect(){
		Context context = mContext.getApplicationContext();
	    ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (connectivity == null) {    
	      return false;
	    } else {
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();
	        if (info != null) {        
	            for (int i = 0; i<info.length; i++) {
	                if (info[i].getState() == NetworkInfo.State.CONNECTED) {              
	                    return true; 
	                }        
	            }     
	        } 
	    }
	    return false;
	}
	
	
	/**
	 * 是否已连接
	 * @return
	 */
	public boolean isConnected(){
		return isConnect();
	}
    
}
