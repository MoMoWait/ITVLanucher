/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月26日 下午9:57:05  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月26日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.model;

import com.rockchip.itvbox.service.impl.WeatherIcon;
import com.rockchip.itvbox.utils.StringUtils;

public class Weather {
	
	private String cityID;//城市ID
	private String cityName;//城市名称
	private String ipAddress;//IP地址
	
	//天气情况
	private String fromTemp;//温度
	private String toTemp;
	private String weather;//天气描述
	private String img1;
	private String img2;
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public boolean hasCity(){
		return StringUtils.hasText(cityName);
	}
	
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the cityID
	 */
	public String getCityID() {
		return cityID;
	}
	/**
	 * @param cityID the cityID to set
	 */
	public void setCityID(String cityID) {
		this.cityID = cityID;
	}
	/**
	 * @return the fromTemp
	 */
	public String getFromTemp() {
		return fromTemp;
	}
	/**
	 * @param fromTemp the fromTemp to set
	 */
	public void setFromTemp(String fromTemp) {
		this.fromTemp = fromTemp;
	}
	/**
	 * @return the toTemp
	 */
	public String getToTemp() {
		return toTemp;
	}
	/**
	 * @param toTemp the toTemp to set
	 */
	public void setToTemp(String toTemp) {
		this.toTemp = toTemp;
	}
	/**
	 * @return the weather
	 */
	public String getWeather() {
		return weather;
	}
	/**
	 * @param weather the weather to set
	 */
	public void setWeather(String weather) {
		this.weather = weather;
	}
	/**
	 * @return the img1
	 */
	public String getImg1() {
		return img1;
	}
	/**
	 * @param img1 the img1 to set
	 */
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	/**
	 * @return the img2
	 */
	public String getImg2() {
		return img2;
	}
	/**
	 * @param img2 the img2 to set
	 */
	public void setImg2(String img2) {
		this.img2 = img2;
	}
	
	/**
	 * 根据weather获取drawable icon
	 */
	public int getFromIcon(){
		int res[] = WeatherIcon.getIconsByWeatherInfo(weather);
		if(res.length>0){
			return res[0];
		}
		return -1;
	}
	
}
