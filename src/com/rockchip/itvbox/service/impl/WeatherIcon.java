/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月27日 下午2:01:16  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月27日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.service.impl;


import com.rockchip.itvbox.R;

public class WeatherIcon {

	public static int[] getIconsByWeatherInfo(String weather) {
		String[] strs = weather.split("转|到");
		int[] resIds = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			resIds[i] = getIconBySplitWeather(strs[i]);
		}
		if (resIds.length == 3) {
			if (resIds[0] == 0) {
				int[] newResids = new int[2];
				newResids[0] = resIds[1];
				newResids[1] = resIds[2];
				resIds = newResids;
			}
		} else if (resIds.length == 1) {
			int[] newResids = new int[2];
			newResids[0] = resIds[0];
			newResids[1] = 0;
			resIds = newResids;
		}
		return resIds;
	}
	
	private static int getIconBySplitWeather(String weather) {
		if (weather.equals("阴")) {
			return R.drawable.ic_weather_cloudy_l;
		} else if (weather.equals("多云")) {
			return R.drawable.ic_weather_partly_cloudy_l;
		} else if (weather.equals("晴")) {
			return R.drawable.ic_weather_clear_day_l;
		} else if (weather.equals("小雨")) {
			return R.drawable.ic_weather_chance_of_rain_l;
		} else if (weather.equals("中雨")) {
			return R.drawable.ic_weather_rain_xl;
		} else if (weather.equals("大雨")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("暴雨")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("大暴雨")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("特大暴雨")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("阵雨")) {
			return R.drawable.ic_weather_chance_storm_l;
		} else if (weather.equals("雷阵雨")) {
			return R.drawable.ic_weather_thunderstorm_l;
		} else if (weather.equals("小雪")) {
			return R.drawable.ic_weather_chance_snow_l;
		} else if (weather.equals("中雪")) {
			return R.drawable.ic_weather_flurries_l;
		} else if (weather.equals("大雪")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("暴雪")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("冰雹")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("风")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("龙卷风")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("雾")) {
			return R.drawable.ic_weather_fog_l;
		}
		return 0;
	}
}
