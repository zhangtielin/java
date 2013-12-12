package com.pubnub.api;

import org.json.JSONObject;

public abstract class MtGoxCallback {
	public abstract void callback(JSONObject data);
}
