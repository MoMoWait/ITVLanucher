/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014��4��3�� ����6:04:43  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014��4��3��      fxw         1.0         create
*******************************************************************/   

package com.rockchip.itvbox.filter;

public interface IAppsFilter {

	public boolean accept(String pkg);
	
}
