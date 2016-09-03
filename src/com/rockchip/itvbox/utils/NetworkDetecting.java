/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    NetworkDetecting.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2011-9-9 ����06:02:42  
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
 * ������
 */
public class NetworkDetecting {
	
	private Context mContext;
	private boolean isConnected;
	private AlertDialog mAlertDialog;
	
	public NetworkDetecting(Context context){
		mContext = context;
	}
	
	/**
	 * ��⵱ǰ����״̬
	 * ������true δ����false
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
	 * ����������ʾ������������
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
	 * �жϵ�ǰ�����Ƿ������ӻ���������
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
	 * �Ƿ�������
	 * @return
	 */
	public boolean isConnected(){
		return isConnect();
	}
    
}
