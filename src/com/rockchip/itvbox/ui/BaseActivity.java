/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年4月15日 下午4:42:08  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年4月15日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.ui;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.rockchip.itvbox.utils.WindowHelper;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;

public class BaseActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowHelper.setFullScreen(getWindow());
		getWindow().setBackgroundDrawable(getBackground());
	}
	
	public Drawable getBackground(){
		return getDrawableFromAssert("main_bg.png");
	}
	
	public Drawable getDrawableFromAssert(String fileName){
		Display display = WindowHelper.getWindowDisplay(getWindow());
		String drawablePath = String.format("drawable-%dx%d/%s", display.getWidth(), display.getHeight(), fileName);
		if(!new File(drawablePath).exists())
		{
			drawablePath = "drawable/"+fileName;
		}
		//file:///android_asset
		InputStream is = null;
		try {
			is = getAssets().open(drawablePath);
			return Drawable.createFromStream(is, "src");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
}
