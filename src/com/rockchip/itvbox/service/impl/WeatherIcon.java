/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��3��27�� ����2:01:16  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��3��27��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.service.impl;


import com.rockchip.itvbox.R;

public class WeatherIcon {

	public static int[] getIconsByWeatherInfo(String weather) {
		String[] strs = weather.split("ת|��");
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
		if (weather.equals("��")) {
			return R.drawable.ic_weather_cloudy_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_partly_cloudy_l;
		} else if (weather.equals("��")) {
			return R.drawable.ic_weather_clear_day_l;
		} else if (weather.equals("С��")) {
			return R.drawable.ic_weather_chance_of_rain_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_rain_xl;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("�ش���")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_chance_storm_l;
		} else if (weather.equals("������")) {
			return R.drawable.ic_weather_thunderstorm_l;
		} else if (weather.equals("Сѩ")) {
			return R.drawable.ic_weather_chance_snow_l;
		} else if (weather.equals("��ѩ")) {
			return R.drawable.ic_weather_flurries_l;
		} else if (weather.equals("��ѩ")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("��ѩ")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("����")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("���ѩ")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("��")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("�����")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("��")) {
			return R.drawable.ic_weather_fog_l;
		}
		return 0;
	}
}
