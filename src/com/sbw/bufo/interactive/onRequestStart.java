/**
 * 
 */
package com.sbw.bufo.interactive;

import org.json.JSONObject;

/**
 * @author Sumanta
 *
 */
public interface onRequestStart {

	public void onRequestStartSuccess(JSONObject jsonObject);
	
	public void onRequestStartFaild();
}
