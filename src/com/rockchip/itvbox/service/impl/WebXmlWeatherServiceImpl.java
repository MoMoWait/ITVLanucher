/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��3��26�� ����9:58:10  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��3��26��      fxw         1.0         create
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

//WebXml���ȶ��ҽϴֲڣ��ݲ�ʵ��

public class WebXmlWeatherServiceImpl implements IWeatherService {
	
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	// WebService��ַ
	private static final String URL = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	private static final String METHOD_NAME = "getWeatherbyCityName";
	private static final String SOAP_ACTION = "http://WebXml.com.cn/getWeatherbyCityName";
	
	/**
	 * ��ȡ��ǰ������Ϣ
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
		String weatherToday = "\n������" + date.split(" ")[1];
		weatherToday = weatherToday + "\n���£�"
				+ detail.getProperty(5).toString();
		weatherToday = weatherToday + "\n������"
				+ detail.getProperty(7).toString() + "\n";

		//String weatherNow = detail.getProperty(8).toString();
		//TODO
		//WebXML���������ȶ����ݲ�ʵ��
	}
	
	/**
	 * ��ȡ����IP��ַ
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
		start = bodystr.indexOf("���ԣ�")+3;
		bodystr = bodystr.substring(start+1);
		String city = resolveAddress(bodystr);
		if(!StringUtils.hasText(city)){
			city = getCityNameByAddr(ipAddr);
		}
		weatherItem.setCityName(city);
		return weatherItem;
	}
	
	//eg.����ʡ������
	private String resolveAddress(String addr){
		if(!StringUtils.hasText(addr)) return null;	
		
		//ֱϽ�д���
		if (addr.startsWith("��")||addr.startsWith("��")||addr.startsWith("��")||addr.startsWith("��")){
			return addr.substring(0,addr.indexOf("��"));
		}
		//�۰�
		if(addr.startsWith("��")){
			return addr.substring(0,addr.indexOf("��"));
		}
		if(addr.startsWith("��")){
			return addr.substring(0,addr.indexOf("��"));
		}
		//�������д���
		if (addr.indexOf("ʡ") != -1) {
			return addr.substring(addr.indexOf("ʡ") + 1, addr.indexOf("��"));
		}
		return null;
	}
	
	/**
	 * ����IP��ַ��ѯ��������
	 */
/*
	private String getCityNameByAddr(String addr){
		String url = "http://whois.pconline.com.cn/ip.jsp?ip="+addr;
		String reqStr = HttpClientUtils.doGetRequestString(url);
		return resolveAddress(reqStr);
	}
	
	
}*/
