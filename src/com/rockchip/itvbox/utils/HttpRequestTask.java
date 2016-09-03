/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��3��27�� ����10:34:30  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��3��27��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.utils;

import android.os.AsyncTask;

public class HttpRequestTask<T> extends AsyncTask<Void, Void, T> {
	
	private HttpRequestListener<T> mListener;
	
	public HttpRequestTask(HttpRequestListener<T> listener){
		this.mListener = listener;
	}

	protected T doInBackground(Void... params) {
		if(mListener!=null){
			return mListener.onRequest();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(T result) {
		if(mListener!=null){
			mListener.onResponse(result);
		}
	}
	
	
	public interface HttpRequestListener<T>{
		
		public T onRequest();
		
		public void onResponse(T result);
		
	}

}
