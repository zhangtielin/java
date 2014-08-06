package com.pubnub.api;

import org.json.JSONObject;


public abstract class DataSyncCallback {
	
	public void onReady(JSONObject data) {
		
	}
	
	public void onUpdate(JSONObject data) {
		
	}

	public void onSet(JSONObject data) {

	}
	
	public void onRemove(JSONObject data) {
		
	}
	
	public void onError(JSONObject data) {
		
	}
	
	public void onNetworkConnect() {
		
	}
	public void onNetworkDisconnect() {
		
	}
	public void onNetworkReconnect() {
		
	}

}
