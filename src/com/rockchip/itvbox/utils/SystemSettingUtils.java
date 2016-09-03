/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��15�� ����2:07:20  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��15��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.utils;

import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;

public class SystemSettingUtils {
	
	public static final String APP_LAUNCH_CONFIG = "launcher_pref";

	/**
	 * ����Ӧ����������
	 */
	public static void increaseAppLaunch(Context context, String pkg){
		SharedPreferences sp = context.getSharedPreferences(APP_LAUNCH_CONFIG, Context.MODE_PRIVATE);
		int launchCnt = sp.getInt(pkg, 0);
		sp.edit().putInt(pkg, launchCnt+1).commit();
	}
	
	/**
	 * ɾ��Ӧ����������
	 */
	public static void removeAppLaunch(Context context, String pkg){
		SharedPreferences sp = context.getSharedPreferences(APP_LAUNCH_CONFIG, Context.MODE_PRIVATE);
		sp.edit().remove(pkg).commit();
	}
	
	/**
	 * ��ȡ����Ӧ�õ���������
	 * @param context
	 * @param launcherMap
	 */
	public static Map<String, Integer> getAppLaunchs(Context context){
		SharedPreferences sp = context.getSharedPreferences(APP_LAUNCH_CONFIG, Context.MODE_PRIVATE);
		Map<String, Integer> launcherMap = new HashMap<String, Integer>();
		Map<String, ?> cfg = sp.getAll();
		for(Map.Entry<String, ?> entry : cfg.entrySet()){
			launcherMap.put(entry.getKey(), Integer.parseInt(String.valueOf(entry.getValue())));
		}
		return launcherMap;
	}
	
}
