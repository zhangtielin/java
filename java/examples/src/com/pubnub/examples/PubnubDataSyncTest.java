package com.pubnub.examples;

import org.json.JSONObject;

import com.pubnub.api.DataSyncCallback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import com.pubnub.api.PubnubSyncedObject;

public class PubnubDataSyncTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pubnub pubnub = new Pubnub("pub-c-bf446f9e-dd7f-43fe-8736-d6e5dce3fe67", "sub-c-d1c2cc5a-1102-11e4-8880-02ee2ddab7fe");
		
		PubnubSyncedObject pso = pubnub.createSyncObject("a.b.c.d");
		
		try {
			pso.initSync(new DataSyncCallback(){
				@Override
				public void onReady(JSONObject data) {
					System.out.println(data);
				}
				@Override
				public void onUpdate(JSONObject data) {
					System.out.println(data);
				}
				@Override
				public void onSet(JSONObject data) {
					System.out.println(data);
				}
				@Override
				public void onRemove(JSONObject data) {
					System.out.println(data);
				}
				@Override
				public void onError(JSONObject data) {
					System.out.println(data);
				}
			});
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
