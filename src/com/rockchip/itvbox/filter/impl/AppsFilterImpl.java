/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年4月3日 下午6:05:21  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年4月3日      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.filter.impl;

import java.text.Collator;
import java.util.Comparator;
import java.util.Map;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.rockchip.itvbox.filter.IAppsFilter;

public class AppsFilterImpl implements IAppsFilter {

	private static final String[] filterApps = {
		"com.android.calculator2",
		"com.android.calendar",
		"com.android.camera",
		"com.android.deskclock",
		"com.android.development",
		"com.android.providers.downloads.ui",
		"com.android.contacts",
		"com.android.quicksearchbox",
		"com.android.spare_parts",
		"com.android.speechrecorder",
		"com.google.android.apps.maps",
		"com.cooliris.media",
		"com.appside.android.VpadMonitor",
		"tv.tv9ikan.app",
		"com.rk.youtube",
		"com.rk.ui.rkxbmc",
		"com.rockchip.itvbox",
		"com.android.apkinstaller",
		"com.rk_itvui.allapp",
		"com.rk.setting",
		"com.rockchip.settings"
		//"com.android.browser",
		//"android.rk.RockVideoPlayer",
		//"com.android.settings"
	};
	
	public boolean accept(String pkg) {
		for(String app : filterApps){
			if(app.equals(pkg)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 *	Apps排序 
	 */
	public static class AppsComparator implements Comparator<ResolveInfo>{
		
		private static final String[] sortApps = {
			"android.rk.RockVideoPlayer",
			"com.android.music",
			"com.rockchip.mediacenter",
			"com.rockchip.wfd",
			"com.android.rockchip",//FileExplorer
			"com.android.settings",
			"com.android.browser"
		};
		private final Collator sCollator = Collator.getInstance();
        private PackageManager mPM;
        private Map<String, Integer> mLaunchMap;
        
		public AppsComparator(PackageManager pm, Map<String, Integer> launchMap){
			mPM = pm;
			mLaunchMap = launchMap;
		}
		
		public int compare(ResolveInfo lhs, ResolveInfo rhs) {
			String lpkg = lhs.activityInfo.packageName;
			String rpkg = rhs.activityInfo.packageName;
			int lcnt = getLaunchCount(lpkg);//left
			int rcnt = getLaunchCount(rpkg);//right
			int lIndex = -1, rIndex = -1;
			for(int i=0; i<sortApps.length; i++){
				if(sortApps[i].equals(lpkg)){
					lIndex = i;
				}else if(sortApps[i].equals(rpkg)){
					rIndex = i;
				}
			}
			
			if(lcnt!=-1&&rcnt!=-1){//以启动次数来排序
				return rcnt-lcnt;//次数多的排前面
			}else if(lcnt!=-1&&rcnt==-1){
				return -1;
			}else if(lcnt==-1&&rcnt!=-1){
				return 1;
			}else if(lIndex!=-1&&rIndex!=-1){//都在优先列表里，按列表顺序
				return lIndex-rIndex;
			}else if(lIndex!=-1&&rIndex==-1){
				return -1;
			}else if(lIndex==-1&&rIndex!=-1){
				return 1;
			}else{
				CharSequence  sa = lhs.loadLabel(mPM);
	            if (sa == null) sa = lhs.activityInfo.name;
	            CharSequence  sb = rhs.loadLabel(mPM);
	            if (sb == null) sb = rhs.activityInfo.name;
	            return sCollator.compare(sa.toString(), sb.toString());
			}
		}
		
		private int getLaunchCount(String pkg){
			Integer value = mLaunchMap.get(pkg);
			return value==null?-1:value;
		}
	}

}
