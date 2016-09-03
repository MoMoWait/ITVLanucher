/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月27日 上午8:57:17  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月27日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.utils;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
	
	public static final int TIMEOUT_MILLISEC = 10000;
	
	
	/**
	 * Do Get Request, then return string result.
	 * @param url
	 * @return
	 */
	public static String doGetRequestString(String url){
		URI mURL = null;
		HttpClient httpClient = getDefaultHttpClient();
		try {
			mURL = new URI(url);
			HttpGet httpget = new HttpGet(mURL);
			HttpResponse httpResponse = httpClient.execute(httpget);
			String result = null;
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				result = EntityUtils.toString(httpResponse.getEntity());
			}
			httpget.abort();
			return result;
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	}
	
	public static HttpClient getDefaultHttpClient(){
		HttpParams httpParams = new BasicHttpParams();    
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);    
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		ConnManagerParams.setTimeout(httpParams, TIMEOUT_MILLISEC/10);
		
		SchemeRegistry schReg = new SchemeRegistry();//设置支持HTTP和HTTPS两种模式
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
		HttpClient httpClient = new DefaultHttpClient(conMgr, httpParams);
		return httpClient;
	}

}
