package com.pubnub.examples.loadtest;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

public class PublishTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		String prefix = (args.length > 0)?args[0] + "-":"";
		int noOfMessages = (args.length > 1)?Integer.parseInt(args[1]):10000;
		int workers = (args.length > 2)?Integer.parseInt(args[2]):10000;
		String origin = (args.length > 3)?args[3]:"pubsub";
		
		Pubnub pubnub = new Pubnub("demo", "demo", workers);
		
	    pubnub.setCacheBusting(false);
        pubnub.setOrigin(origin);
			 //pn.setDomain("pubnub.co");  // only if required
		  

		JSONObject[] jsoArray = new JSONObject[noOfMessages];
		
		for ( int i = 0; i < noOfMessages; i++) {
			JSONObject jso = new JSONObject();
			try {
				jso.put("channel", "channel-" + prefix + (i+1));
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
