/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Filename:    ImageCache.java  
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014-3-28
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014-3-28      fxw         1.0         create
*******************************************************************/   


package com.rockchip.itvbox.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 *
 * @author fxw
 * @since 1.0
 */
public interface IImageCache<K> {
	
	public void putDrawable(K key, Drawable drawable);

	public void putBitmap(K key, Bitmap bitmap);
	
	public Bitmap getBitmap(K key);
	
	public boolean hasBitmap(String key);
	
	public void remove(K key);
	
	public void clear();
	
	public String getRealCacheDirectory();
	
}
