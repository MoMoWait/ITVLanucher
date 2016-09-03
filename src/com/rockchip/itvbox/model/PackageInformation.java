package com.rockchip.itvbox.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;

public class PackageInformation
{
	public static final String K_APP_NAME = "appName";
	public static final String K_PKG_NAME = "pkgName";
	public static final String K_ACTIVITY = "activity";
	
	private String appName = null;
	private String pkgName = null;
	private String activity = null;
	private Drawable appIcon = null;
	
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	/**
	 * @return the pkgName
	 */
	public String getPkgName() {
		return pkgName;
	}
	/**
	 * @param pkgName the pkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	/**
	 * @return the activity
	 */
	public String getActivity() {
		return activity;
	}
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(String activity) {
		this.activity = activity;
	}
	/**
	 * @return the appIcon
	 */
	public Drawable getAppIcon() {
		return appIcon;
	}
	/**
	 * @param appIcon the appIcon to set
	 */
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(K_APP_NAME, appName);
		json.put(K_PKG_NAME, pkgName);
		json.put(K_ACTIVITY, activity);
		return json;
	}
	
}