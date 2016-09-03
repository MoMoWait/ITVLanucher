/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年4月15日 下午2:07:20  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年4月15日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.utils;

import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.content.SharedPreferences;

public class SystemSettingUtils {
	
	public static final String APP_LAUNCH_CONFIG = "launcher_pref";

	/**
	 * 递增应用启动次数
	 */
	public static void increaseAppLaunch(Context context, String pkg){
		SharedPreferences sp = context.getSharedPreferences(APP_LAUNCH_CONFIG, Context.MODE_PRIVATE);
		int launchCnt = sp.getInt(pkg, 0);
		sp.edit().putInt(pkg, launchCnt+1).commit();
	}
	
	/**
	 * 删除应用启动次数
	 */
	public static void removeAppLaunch(Context context, String pkg){
		SharedPreferences sp = context.getSharedPreferences(APP_LAUNCH_CONFIG, Context.MODE_PRIVATE);
		sp.edit().remove(pkg).commit();
	}
	
	/**
	 * 获取所有应用的启动次数
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
