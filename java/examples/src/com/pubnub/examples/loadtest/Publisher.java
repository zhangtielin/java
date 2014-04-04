package com.pubnub.examples.loadtest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

class PublisherRunnable implements Runnable {
	String id = "";
	JSONArray messageArray;
	Pubnub pubnub;
	Publisher publisher;
	volatile boolean start = false;
	
	public void run() {
		
		while(!start) {
			//System.out.println(start);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < messageArray.length(); i++) {
			try {
				final JSONObject message = messageArray.getJSONObject(i);
				String channel = message.getString("channel");
				message.put("publisher-thread-id", id);
				message.put("publishTimestamp", System.currentTimeMillis());
				
				pubnub.publish(channel, message, new Callback(){
					@Override
					public void successCallback(String channel, Object response) {
						try {
							message.put("publishSuccessTimestamp", System.currentTimeMillis());
							message.put("publishTime", message.getInt("publishSuccessTimestamp")  - message.getInt("publishTimestamp"));
							message.put("threadId", Thread.currentThread().getId());
							System.out.println(message);
							publisher.gotResponse(message);
						} catch (JSONException e) {
							System.out.println(e);
						}
					}
					@Override
					public void errorCallback(String channel, PubnubError error) {
						
					}
				});
				
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		
	}
	
	PublisherRunnable(Publisher publisher, Pubnub pubnub, String id, JSONArray messageArray) {
		this.id = id;
		this.messageArray = messageArray;
		this.pubnub = pubnub;
		this.publisher = publisher;
	}
	
}


class Publisher {
	private Pubnub pubnub;
	private JSONObject[] messages;
	private JSONArray responses;
	private int id;
	private int noOfThreads = 1;
	private PublisherRunnable[] pubRunnables;
	private Thread[] threads;
	private int messagesPerThread = 0;
	private int messageIndex = 0;
	private long startTimestamp = 0L;
	private long endTimestamp = 0L;
	private int publishResponses = 0;
	
	Publisher(int id, int noOfThreads, Pubnub pubnub, JSONObject[] messages) {
		this.messages = messages;
		this.pubnub = pubnub;
		this.responses = new JSONArray();
		this.noOfThreads = noOfThreads;
		this.id = id;
		this.messagesPerThread = this.messages.length / this.noOfThreads;
		this.threads = new Thread[this.noOfThreads];
		this.pubRunnables = new PublisherRunnable[this.noOfThreads];
	}
	
	void init() {
		for (int i = 0; i < this.noOfThreads; i++) {
			int messagesLeft = this.messages.length - this.messageIndex;
			int messageCount = (messagesLeft - this.messageIndex > this.messagesPerThread)?this.messagesPerThread: messagesLeft;
			JSONArray messageArray = new JSONArray();
			
			for (int j = 0; j < messageCount; j++) {
				messageArray.put(this.messages[j + this.messageIndex]);
			}
			this.messageIndex += messageCount;
			pubRunnables[i] = new PublisherRunnable(this, this.pubnub, "pub_thread-" + (i + 1) + "-" + id,messageArray);
			threads[i] = new Thread(pubRunnables[i]);
		}
	}
	
	void start() {
		
		for (int i = 0; i < this.noOfThreads; i++) {
			threads[i].start();
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		startTimestamp = System.currentTimeMillis();
		for (int i = 0; i < this.noOfThreads; i++) {
			pubRunnables[i].start = true;
			//System.out.println(threads[i].start);
		}
		
	}
	synchronized void gotResponse(JSONObject response) {
		this.responses.put(response);
		if ( this.responses.length() == this.messages.length) {
			this.endTimestamp = System.currentTimeMillis();
			System.err.println("Time taken in milliseconds for " + this.messages.length + " messages to publish successfully : " + (this.endTimestamp - this.startTimestamp));
			pubnub.shutdown();
		}
	}
	
	
	
	
}
