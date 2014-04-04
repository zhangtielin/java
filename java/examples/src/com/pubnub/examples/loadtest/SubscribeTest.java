package com.pubnub.examples.loadtest;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Pubnub;

public class SubscribeTest {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String prefix = (args.length > 0)?args[0] + "-":"";
		int noOfChannels = (args.length > 1)?Integer.parseInt(args[1]):10000;
		int workers = (args.length > 2)?Integer.parseInt(args[2]):10000;
		
		String[] channels = new String[noOfChannels];
		
		for ( int i = 0; i < noOfChannels; i++) {
			channels[i] = "channel-" + prefix + (i+1);
		}
		
		Subscriber subscriber = new Subscriber(1234, channels, workers);
		subscriber.init();
		subscriber.start();
	}
}
