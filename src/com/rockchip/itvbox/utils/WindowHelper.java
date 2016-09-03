/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��15�� ����4:48:16  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��15��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.utils;

import android.app.Activity;
import android.os.Build;
import android.view.Display;
import android.view.Window;

public class WindowHelper {

	/**
	 * ����ȫ��
	 * @param window
	 */
    public static void setFullScreen(Window window){
    	if(Build.VERSION.SDK_INT<=14) return;
    	int flag = getViewStaticProperty("SYSTEM_UI_FLAG_FULLSCREEN")
    			|getViewStaticProperty("SYSTEM_UI_FLAG_HIDE_NAVIGATION")
    			|getViewStaticProperty("SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN")
    			|getViewStaticProperty("SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION")
    			|getViewStaticProperty("SYSTEM_UI_FLAG_IMMERSIVE_STICKY");
    	ReflectionUtils.invokeMethod(window.getDecorView(), "setSystemUiVisibility",new Class[]{int.class}, flag);	
    }
    private static int getViewStaticProperty(String prop){
    	Object ret = ReflectionUtils.getStaticFieldValue("android.view.View", prop);
    	return ret==null?0:(Integer)ret;
    }
    
    /**
     * ��ȡWindow���
     */
	public static Display getWindowDisplay(Window window){
		return window.getWindowManager().getDefaultDisplay();
	}
	public static Display getWindowDisplay(Activity activity){
		return activity.getWindowManager().getDefaultDisplay();
	}
}
