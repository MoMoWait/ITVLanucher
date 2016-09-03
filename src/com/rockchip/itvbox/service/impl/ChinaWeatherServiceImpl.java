/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月27日 上午10:24:46  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月27日      fxw         1.0         create
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
	public static final String DEFAULT_CITYCODE = "101010100"; //默认北京

	/**
	 * 获取当前天气
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
	 * 粗略定位城市
	 * TODO 在settings中增加设置城市
	 * @return 定位失败，返回默认城市
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
	 * 通过Sina Ip定位当前城市
	 * @return
	 */
	public static String getRoughlyLocation(Context context) {
		//1. 请求位置
		String location[] = new String[4];
		String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
		String json = HttpClientUtils.doGetRequestString(url);
		if (json == null) {
			return null;
		}
		try {
			JSONObject jobj = new JSONObject(json);
			location[0] = jobj.getString("country");// 国
			location[1] = jobj.getString("province");// 省
			location[2] = jobj.getString("city");// 市
			location[3] = jobj.getString("district");// 区
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// 2.将获取到的位置转化为城市编码
		SQLiteDatabase weatherDb = null;
		File dbFile = null;
		try{
			dbFile = context.getDatabasePath(WEATHER_DB);
			if(!dbFile.exists()){
				copyWeatherDatabase(context);
			}
			weatherDb = SQLiteDatabase.openDatabase(dbFile.toString(), null, SQLiteDatabase.OPEN_READONLY);
		}catch(Exception e){//重新复制一次数据库
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
	 * 复制城市名称代码的数据库文件至Apk数据库目录
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
			FileOutputStream os = new FileOutputStream(dbFile);// 得到数据库文件的写入流
			InputStream is = context.getAssets().open(WEATHER_DB);// 得到数据库文件的数据流
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
