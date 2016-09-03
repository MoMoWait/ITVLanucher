/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月26日 下午9:53:32  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月26日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.service;

import android.content.Context;

import com.rockchip.itvbox.model.Weather;

public interface IWeatherService {

	public String getCityCode(Context context);
	public Weather getCurrentWeather(String cityCode);
	
}
