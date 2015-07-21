/**
 * 
 */
package com.sbw.bufo.interactive;

import org.json.JSONObject;

/**
 * @author Sumanta
 *
 */
public interface onRequestEnd {

	public void onRequestEndSuccess(JSONObject jsonObject);
	
	public void onRequestEndFaild();
}
