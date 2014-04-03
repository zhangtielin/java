package com.pubnub.examples.loadtest;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

public class PublishTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pubnub pubnub = new Pubnub("demo", "demo", 100);
		
		
			 pubnub.setCacheBusting(false);
			 pubnub.setOrigin("pubsub");
			 //pn.setDomain("pubnub.co");  // only if required
		  
		 
		
		int noOfMessages = 100;
		JSONObject[] jsoArray = new JSONObject[noOfMessages];
		
		for ( int i = 0; i < noOfMessages; i++) {
			JSONObject jso = new JSONObject();
			try {
				jso.put("channel", "channel-" + (i+1));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsoArray[i] = jso;
		}
		
		Publisher publisher = new Publisher(1234, noOfMessages, pubnub, jsoArray);
		publisher.init();
		publisher.start();
	}

}
