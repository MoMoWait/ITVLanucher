/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014-3-28
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014-3-28      fxw         1.0         create
*******************************************************************/   


package com.rockchip.itvbox.cache.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.cocos2dx.lib.Cocos2dxHelper;

import com.rockchip.itvbox.cache.IImageCache;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 *
 * @author fxw
 * @since 1.0
 */
public class AppIconCache implements IImageCache<String> {
	public static final String TAG = AppIconCache.class.getSimpleName();
	private static final String CACHE_DIR = "icon";
	private String mCacheDir;
	
	public void setCacheDirectory(String path){
		mCacheDir = path;
	}
	
	public void setCacheDirectory(File dir){
		mCacheDir = dir.getPath();
	}
	
	/**
	 * 文件存在且大小相同，则不再进行
	 */
	public void putBitmap(String key, Bitmap bitmap) {
		File destFile = getDestinationFile(key);
		if(!destFile.getParentFile().exists()){
			destFile.getParentFile().mkdirs();
		}
		try {
			if(!destFile.exists()){
				destFile.createNewFile();
			}else{
			/*	if(destFile.length() > 0)
					return;*/
				try{
					destFile.delete();
				}catch (Exception e){
					Log.i(TAG, "delete dest file error:" + e);
				}
			}
			FileOutputStream fos = new FileOutputStream(destFile);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			//logger.debug("Save cache. key: " + key);
			
			Log.i(TAG, "putBitmap->destFile->path:" + destFile.getAbsolutePath());
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	public void putDrawable(String key, Drawable drawable) {
		Bitmap bitmap;
		if(drawable instanceof BitmapDrawable){
			bitmap = ((BitmapDrawable) drawable).getBitmap();
		}else{
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			Bitmap.Config config =  drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
			bitmap = Bitmap.createBitmap(w, h, config);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			//canvas.setBitmap(null);
		}
		putBitmap(key, bitmap);
	}

	public Bitmap getBitmap(String key) {
		File destFile = getDestinationFile(key);
		if(destFile.exists()){
			//logger.debug("Get cache. key: " + key);
			return BitmapFactory.decodeFile(destFile.getPath());
		}
		return null;
	}
	
	public boolean hasBitmap(String key){
		File destFile = getDestinationFile(key);
		return destFile.exists();
	}

	public void remove(String key) {
		File destFile = getDestinationFile(key);
		if(destFile.exists()){
			destFile.delete();
		}
	}

	public void clear() {
		File destDir = new File(getRealCacheDirectory());
		if(destDir.exists()){
			File[] files = destDir.listFiles();
			if(files!=null){
				for(File item : files){
					item.delete();
				}
			}
		}
	}
	
	public String getRealCacheDirectory(){
		if(mCacheDir==null){
			mCacheDir = Cocos2dxHelper.getCocos2dxWritablePath();
		}
		return mCacheDir+File.separator+CACHE_DIR;
	}
	
	private File getDestinationFile(String pkg){
		String fileName = pkg.replace('.', '&');
		String fileDir = getRealCacheDirectory();
		return new File(fileDir+File.separator+fileName);
	}

}
