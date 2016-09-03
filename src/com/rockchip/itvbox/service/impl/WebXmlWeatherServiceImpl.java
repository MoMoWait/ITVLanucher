/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年3月26日 下午9:58:10  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年3月26日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.service.impl;

/*
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.rockchip.itvbox.model.Weather;
import com.rockchip.itvbox.service.IWeatherService;
import com.rockchip.itvbox.utils.HttpClientUtils;
import com.rockchip.itvbox.utils.StringUtils;

//WebXml不稳定且较粗糙，暂不实现

public class WebXmlWeatherServiceImpl implements IWeatherService {
	
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	// WebService地址
	private static final String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	private static final String METHOD_NAME = "getWeatherbyCityName";
	private static final String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";
	
	/**
	 * 获取当前天气信息
	 */
/*
	public Weather getCurrentWeather() {
		Weather weather = getCityInfo();
		if(!weather.hasCity()){
			return weather;
		}
		SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
		rpc.addProperty("theCityName", weather.getCityName());

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(URL);
		ht.debug = true;
		try {
			ht.call(SOAP_ACTION, envelope);
			SoapObject detail = (SoapObject) envelope.getResponse();
			parseWeather(weather, detail);
		} catch (Exception e) {
		}
		return weather;
	}
	private void parseWeather(Weather weather, SoapObject detail){
		String date = detail.getProperty(6).toString();
		String weatherToday = "\n天气：" + date.split(" ")[1];
		weatherToday = weatherToday + "\n气温："
				+ detail.getProperty(5).toString();
		weatherToday = weatherToday + "\n风力："
				+ detail.getProperty(7).toString() + "\n";

		//String weatherNow = detail.getProperty(8).toString();
		//TODO
		//WebXML天气服务不稳定，暂不实现
	}
	
	/**
	 * 获取城市IP地址
	 */
/*
	public Weather getCityInfo() {
		Weather weatherItem = new Weather();
		String reqStr = HttpClientUtils.doGetRequestString("http://city.ip138.com/city.asp");
		if(!StringUtils.hasText(reqStr)) return null;	
		Document doc = Jsoup.parse(reqStr);
		String bodystr = doc.body().text();
		int start = bodystr.indexOf("[");
		int end = bodystr.indexOf("]");
		String ipAddr = bodystr.substring(start + 1, end);
		weatherItem.setIpAddress(ipAddr);
		start = bodystr.indexOf("来自：")+3;
		bodystr = bodystr.substring(start+1);
		String city = resolveAddress(bodystr);
		if(!StringUtils.hasText(city)){
			city = getCityNameByAddr(ipAddr);
		}
		weatherItem.setCityName(city);
		return weatherItem;
	}
	
	//eg.福建省福州市
	private String resolveAddress(String addr){
		if(!StringUtils.hasText(addr)) return null;	
		
		//直辖市处理
		if (addr.startsWith("北")||addr.startsWith("上")||addr.startsWith("重")||addr.startsWith("天")){
			return addr.substring(0,addr.indexOf("市"));
		}
		//港澳
		if(addr.startsWith("香")){
			return addr.substring(0,addr.indexOf("港"));
		}
		if(addr.startsWith("澳")){
			return addr.substring(0,addr.indexOf("门"));
		}
		//其他城市处理
		if (addr.indexOf("省") != -1) {
			return addr.substring(addr.indexOf("省") + 1, addr.indexOf("市"));
		}
		return null;
	}
	
	/**
	 * 根据IP地址查询城市名称
	 */
/*
	private String getCityNameByAddr(String addr){
		String url = "http://whois.pconline.com.cn/ip.jsp?ip="+addr;
		String reqStr = HttpClientUtils.doGetRequestString(url);
		return resolveAddress(reqStr);
	}
	
	
}*/
