package com.pubnub.examples.loadtest;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Pubnub;

public class SubscribeTest {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int noOfChannels = 100;
		String[] channels = new String[noOfChannels];
		
		for ( int i = 0; i < noOfChannels; i++) {
			channels[i] = "channel-" + (i+1);
		}
		
		Subscriber subscriber = new Subscriber(1234, channels, 100);
		subscriber.init();
		subscriber.start();
	}
}
