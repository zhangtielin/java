package com.pubnub.examples.loadtest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

class SubscriberRunnable implements Runnable {
	String[] channels;
	String id = "";
	Pubnub pubnub;
	Subscriber subscriber;
	Long timetoken = 0L;
	Long starttime = 0L;
	SubscriberRunnable(Subscriber subscriber, Pubnub pubnub, String id, String[] channels, long timetoken) {
		this.channels = channels;
		this.id = id;
		this.pubnub = pubnub;
		this.subscriber = subscriber;
		this.timetoken = timetoken;
	}
	public void run() {
		try {
			starttime = System.currentTimeMillis();
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
					long connecttime = System.currentTimeMillis();
                    System.out.println(channel+" connected "+  connecttime +

                            " time since start "+ (connecttime - starttime));
				}
				@Override
				public void errorCallback(String channel, PubnubError error) {
					System.out.println("ERROR on channel : " + channel + " , " + error);
					subscriber.errors(channel);
				}

			}, timetoken);
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
	String publish_key = "demo";
	String subscribe_key = "demo";
	String secret_key = "demo";
	String cipher_key = "demo";
	String auth_key = null;
	boolean ssl = false;
	private int channelsPerThread;
	private SubscriberRunnable[] subRunnables;
	private Thread[] threads;
	int channelsIndex = 0;
	long firstTimestamp = 0L;
	long timetoken = 0L;

	synchronized void connected(String channel) {
		connectedChannels.add(channel);
		if (connectedChannels.size() == channels.length) {
			System.err.println("ALL CHANNELS CONNECTED . Count : " + connectedChannels.size());
		}
	}
	synchronized void received(String channel) {
		receivedChannels.add(channel);
		if (firstTimestamp == 0) {
			firstTimestamp = System.currentTimeMillis();
		}/*
		System.out.println("Received = " + receivedChannels.size());
		System.out.println("Errors = " + errorChannels.size());
		*/
		if ( inputChannels.size() - (receivedChannels.size() + errorChannels.size()) < 50 ) {
			System.err.println("Received = " + receivedChannels.size() + ", Errors = " + errorChannels.size());
		}
		
		if (receivedChannels.size() + errorChannels.size() == channels.length) {
			System.err.println("MESSAGES FOR ALL CHANNELS RECEIVED . SUCCESS : " + receivedChannels.size() + ", ERRORS : " + errorChannels.size());
			System.err.println("Difference ( in ms ) between timestamp of last and first received message : " + 
					(System.currentTimeMillis() - firstTimestamp));
		}
	}

	synchronized void errors(String channel) {
		errorChannels.add(channel);
		/*
		System.out.println("Received = " + receivedChannels.size());
		System.out.println("Errors = " + errorChannels.size());
		*/ 
		if ( inputChannels.size() - (receivedChannels.size() + errorChannels.size()) < 50 ) {
			System.err.println("Received = " + receivedChannels.size() + ", Errors = " + errorChannels.size());
		}
		if (receivedChannels.size() + errorChannels.size() == channels.length) {
			System.err.println("MESSAGES FOR ALL CHANNELS RECEIVED . SUCCESS : " + receivedChannels.size() + ", ERRORS : " + errorChannels.size());
		}
	}



	Subscriber(int id, String[] channels, int noOfThreads, 
			String origin, String publish_key, String subscribe_key, String secret_key, 
			String cipher_key, boolean ssl, Long timetoken, String auth_key) {
		this.id = id;
		this.channels = channels;
		this.noOfThreads = noOfThreads;
		this.channelsPerThread = this.channels.length / this.noOfThreads;
		this.subRunnables = new SubscriberRunnable[this.noOfThreads];
		this.threads = new Thread[this.noOfThreads];
		this.origin = origin;
		this.publish_key = publish_key;
		this.subscribe_key = subscribe_key;
		this.secret_key = secret_key;
		this.cipher_key = cipher_key;
		this.ssl = ssl;
		this.timetoken = timetoken;
		this.auth_key = auth_key;
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
			Pubnub pn = new Pubnub(publish_key, subscribe_key, secret_key, cipher_key, ssl);

			pn.setCacheBusting(false);
			pn.setOrigin(origin);
			if (auth_key != null) pn.setAuthKey(auth_key);
			/*
			 pn.setDomain("pubnub.co");  // only if required

			 */
			subRunnables[i] = new SubscriberRunnable(this, pn, "sub_thread-" + (i + 1) + "-" + id,ch, timetoken);
			threads[i] = new Thread(subRunnables[i]);
		}
	}

	void start() {
		for (int i = 0; i < this.noOfThreads; i++) {
			//threads[i].start();
			subRunnables[i].run(); // add serially for now 
		}
	}

}
