/**
 * 
 */
package com.sbw.bufo.interactive;

import org.json.JSONObject;

/**
 * @author Sumanta
 *
 */
public interface onCardStatus {

	public void onCardStatusSuccess(JSONObject respinse);
	
	public void onCardStatusFalse(JSONObject respinse);
}
