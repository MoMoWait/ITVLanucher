/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��3��27�� ����10:24:46  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��3��27��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.rockchip.itvbox.model.Weather;
import com.rockchip.itvbox.service.IWeatherService;
import com.rockchip.itvbox.utils.HttpClientUtils;
import com.rockchip.itvbox.utils.StringUtils;

public class ChinaWeatherServiceImpl implements IWeatherService {
	
	public static final String TAG = "ChinaWeatherServiceImpl";
	public static final String WEATHER_DB = "weather.db";
	public static final String DEFAULT_CITYCODE = "101010100"; //Ĭ�ϱ���

	/**
	 * ��ȡ��ǰ����
	 */
	public Weather getCurrentWeather(String cityCode) {
		String url = "http://www.weather.com.cn/data/cityinfo/" + cityCode + ".html";
		String json = HttpClientUtils.doGetRequestString(url);
		if (json != null) {
			try {
				Weather weather = new Weather();
				JSONObject jsonObject = new JSONObject(json);
				JSONObject jsonInfro = jsonObject.getJSONObject("weatherinfo");
				weather.setCityID(jsonInfro.getString("cityid"));
				weather.setCityName(jsonInfro.getString("city"));
				weather.setFromTemp(jsonInfro.getString("temp1"));
				weather.setToTemp(jsonInfro.getString("temp2"));
				weather.setWeather(jsonInfro.getString("weather"));
				weather.setImg1(jsonInfro.getString("img1"));
				weather.setImg2(jsonInfro.getString("img2"));
				Log.d(TAG, "Request weather, cityCode="+cityCode+", cityName="+weather.getCityName()+", weather="+weather.getWeather());
				return weather;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	

	/**
	 * ���Զ�λ����
	 * TODO ��settings���������ó���
	 * @return ��λʧ�ܣ�����Ĭ�ϳ���
	 */
	public String getCityCode(Context context) {
		String cityCodeAuto = null;
		try{
			cityCodeAuto = getRoughlyLocation(context);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (cityCodeAuto != null) {
			return cityCodeAuto;
		} else {
			return DEFAULT_CITYCODE;
		}
	}

	/**
	 * ͨ��Sina Ip��λ��ǰ����
	 * @return
	 */
	public static String getRoughlyLocation(Context context) {
		//1. ����λ��
		String location[] = new String[4];
		String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
		String json = HttpClientUtils.doGetRequestString(url);
		if (json == null) {
			return null;
		}
		try {
			JSONObject jobj = new JSONObject(json);
			location[0] = jobj.getString("country");// ��
			location[1] = jobj.getString("province");// ʡ
			location[2] = jobj.getString("city");// ��
			location[3] = jobj.getString("district");// ��
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 2.����ȡ����λ��ת��Ϊ���б���
		SQLiteDatabase weatherDb = null;
		File dbFile = null;
		try{
			dbFile = context.getDatabasePath(WEATHER_DB);
			if(!dbFile.exists()){
				copyWeatherDatabase(context);
			}
			weatherDb = SQLiteDatabase.openDatabase(dbFile.toString(), null, SQLiteDatabase.OPEN_READONLY);
		}catch(Exception e){//���¸���һ�����ݿ�
			copyWeatherDatabase(context);
			try{
				weatherDb = SQLiteDatabase.openDatabase(dbFile.toString(), null, SQLiteDatabase.OPEN_READONLY);
			}catch(SQLiteException ex){
				return null;
			}
		}
		
		Cursor cursor = null;
		if(StringUtils.hasText(location[3])) {
			cursor = weatherDb.query("citys", new String[] { "city_num" },
					"name=?", new String[] { location[2] + "." + location[3] },
					null, null, null);
		} else {
			cursor = weatherDb.query("citys", new String[] { "city_num" },
					"name=?", new String[] { location[2] }, null, null, null);
		}
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			String citycode = cursor.getString(cursor.getColumnIndex("city_num"));
			cursor.close();
			weatherDb.close();
			return citycode;
		} else {
			return null;
		}
	}
	
	/**
	 * ���Ƴ������ƴ�������ݿ��ļ���Apk���ݿ�Ŀ¼
	 * @param context
	 */
	private static void copyWeatherDatabase(Context context) {
		byte[] buf = new byte[20480];
		try {
			File dbFile = context.getDatabasePath(WEATHER_DB);
			if(!dbFile.exists()){
				File parent = new File(dbFile.getParent());
				if(!parent.exists()){
					parent.mkdirs();
				}
				dbFile.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(dbFile);// �õ����ݿ��ļ���д����
			InputStream is = context.getAssets().open(WEATHER_DB);// �õ����ݿ��ļ���������
			int cnt = -1;
			while ((cnt = is.read(buf)) != -1) {
				os.write(buf, 0, cnt);
			}
			os.flush();
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			buf = null;
		}
	}
	
	

}
