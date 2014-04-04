package com.pubnub.examples.loadtest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

class SubscriberThread implements Runnable {
	String[] channels;
	String id = "";
	Pubnub pubnub;
	Subscriber subscriber;
	SubscriberThread(Subscriber subscriber, Pubnub pubnub, String id, String[] channels) {
		this.channels = channels;
		this.id = id;
		this.pubnub = pubnub;
		this.subscriber = subscriber;
	}
	public void run() {
		try {
			pubnub.subscribe(this.channels, new Callback(){
				@Override
				public void successCallback(String channel, Object response) {
					JSONObject jso = (JSONObject)response;
					long subscriberReceiveTimestamp = System.currentTimeMillis();
					try {
						Long publishTimestamp = (Long) jso.get("publishTimestamp");
						jso.put("subscriberReceiveTimestamp", subscriberReceiveTimestamp);
						jso.put("timeTakenToRecieveAfterPublish", subscriberReceiveTimestamp - publishTimestamp);
						System.out.println(jso);
						subscriber.received(channel);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				@Override
				public void connectCallback(String channel, Object response) {
					subscriber.connected(channel);
				}
				@Override
				public void errorCallback(String channel, PubnubError error) {
					System.out.println("ERROR on channel : " + channel);
					subscriber.errors(channel);
				}
				
			});
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}


class Subscriber {
	Set<String> connectedChannels = new LinkedHashSet<String>();
	Set<String> receivedChannels = new LinkedHashSet<String>();
	Set<String> errorChannels = new LinkedHashSet<String>();
	Set<String> inputChannels = new LinkedHashSet<String>();
	String[] channels;
	int noOfChannels;
	int noOfThreads;
	int id;
	String origin = "pubsub";
	private int channelsPerThread;
	private SubscriberThread[] threads;
	int channelsIndex = 0;
	
	synchronized void connected(String channel) {
		connectedChannels.add(channel);
		if (connectedChannels.size() == channels.length) {
			System.err.println("ALL CHANNELS CONNECTED . Count : " + connectedChannels.size());
		}
	}
	synchronized void received(String channel) {
		receivedChannels.add(channel);
		System.out.println("Received = " + receivedChannels.size());
		System.out.println("Errors = " + errorChannels.size());
                if ( inputChannels.size() - (receivedChannels.size() + errorChannels.size()) < 100 ) {
		    System.err.println("Received = " + receivedChannels.size());
		    System.err.println("Errors = " + errorChannels.size());

                }
		if (receivedChannels.size() + errorChannels.size() == channels.length) {
			System.err.println("MESSAGES FOR ALL CHANNELS RECEIVED . SUCCESS : " + receivedChannels.size() + ", ERRORS : " + errorChannels.size());
		}
	}
	
	synchronized void errors(String channel) {
		errorChannels.add(channel);
		System.out.println("Received = " + receivedChannels.size());
		System.out.println("Errors = " + errorChannels.size());
		if (receivedChannels.size() + errorChannels.size() == channels.length) {
			System.err.println("MESSAGES FOR ALL CHANNELS RECEIVED . SUCCESS : " + receivedChannels.size() + ", ERRORS : " + errorChannels.size());
		}
	}
	
	
	
	Subscriber(int id, String[] channels, int noOfThreads, String origin) {
		this.id = id;
		this.channels = channels;
		this.noOfThreads = noOfThreads;
		this.channelsPerThread = this.channels.length / this.noOfThreads;
		this.threads = new SubscriberThread[this.noOfThreads];
		this.origin = origin;
	}
	
	void init() {
		for (int i = 0; i < this.noOfThreads; i++) {
			int channelsLeft = this.channels.length - this.channelsIndex;
			int channelsCount = (channelsLeft > this.channelsPerThread)?this.channelsPerThread: channelsLeft;
			String[] ch = new String[channelsCount];
			
			for (int j = 0; j < channelsCount; j++) {
				ch[j] = this.channels[j + this.channelsIndex];
				inputChannels.add(ch[j]);
			}
			this.channelsIndex += channelsCount;
			Pubnub pn = new Pubnub("demo", "demo");
			
			 pn.setCacheBusting(false);
			 pn.setOrigin(origin);
			 /*
			 pn.setDomain("pubnub.co");  // only if required
			  
			 */
			threads[i] = new SubscriberThread(this, pn, "sub_thread-" + (i + 1) + "-" + id,ch);
		}
	}
	
	void start() {
		for (int i = 0; i < this.noOfThreads; i++) {
			threads[i].run();
		}
	}

}
