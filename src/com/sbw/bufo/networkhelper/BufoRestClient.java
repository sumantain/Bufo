/**
 * 
 */
package com.sbw.bufo.networkhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/**
 * @author SBanerjee
 *
 */
public class BufoRestClient {

	public static String getInstantData(String web)throws Exception{
		BufferedReader in = null;
		String Data = null;
		try{
			HttpClient client = new DefaultHttpClient();
			URI link =new URI(web);
			HttpGet request = new HttpGet();
			request.setURI(link);
			
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer();
			String line = " ";
			String NL = System.getProperty("line.seperaor");
			while((line = in.readLine())!=null){
				sb.append(line+NL);
			}
			in.close();
			Data = sb.toString();
			return Data;
		}catch(Exception e){
			Log.v("Error",e.toString());
		}finally{
			if(in!=null){
				try{
					in.close();
					return Data;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return Data;
	}
	
	public static String postData(String url, String jobj){
		StringBuilder sb = null;
		try {
			HttpParams httpParams = new BasicHttpParams();

			HttpClient client = new DefaultHttpClient(httpParams);
			HttpPost request = new HttpPost(url); // add your url here...

			request.setHeader("Content-Type", "application/json");

			StringEntity se = new StringEntity(jobj.toString());
			
			se.setContentEncoding("UTF-8");
			se.setContentType("application/json");

			request.setEntity(se);

			HttpResponse response = client.execute(request);
			

			InputStream is = response.getEntity().getContent();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {

		}
		return sb.toString();
	}

}
