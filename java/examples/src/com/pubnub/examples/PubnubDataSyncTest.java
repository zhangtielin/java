package com.pubnub.examples;

import org.json.JSONException;
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
		pubnub.setCacheBusting(false);
		pubnub.setOrigin("pubsub-beta");
		final PubnubSyncedObject pso = pubnub.createSyncObject("a.b.c.d");
		
		try {
			pso.initSync(new DataSyncCallback(){
				@Override
				public void onReady(JSONObject data) {
					System.out.println("onReady");
					System.out.println(data);
					try {
						System.out.println(pso.toString(2));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onUpdate(JSONObject data) {
					System.out.println("onUpdate");
					System.out.println(data);
					try {
						System.out.println(pso.toString(2));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onSet(JSONObject data) {
					System.out.println("onSet");
					System.out.println(data);
					try {
						System.out.println(pso.toString(2));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onRemove(JSONObject data) {
					System.out.println("onRemove");
					System.out.println(data);
					try {
						System.out.println(pso.toString(2));
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
				@Override
				public void onError(JSONObject data) {
					System.out.println("onError");
					System.out.println(data);
				}
			});
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
