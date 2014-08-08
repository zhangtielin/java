package com.pubnub.examples;

import com.pubnub.api.*;
import org.json.*;

public class GetData {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Pubnub pubnub = new Pubnub(
                "PUBLISH_KEY",
                "SUBSCRIBE_KEY");

        pubnub.setCacheBusting(false);
        pubnub.setOrigin("pubsub-beta");

        String objectId = "a";

        final PubnubSyncedObject myData = pubnub.createSyncObject(objectId);

        try {
            myData.initSync(new Callback() {

                // Called when the initialization process connects the ObjectID
                // to PubNub
                @Override
                public void connectCallback(String channel, Object message) {
                    System.out.println("CONNECTED : Object Initialized : " + message);
                    /*
                    try {

                        System.err.println(myData.toString());
                        System.out.println(myData.toString(2));
                    } catch (org.json.JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    */

                }

                // Called every time ObjectID is changed, starting with the
                // initialization process
                // that retrieves current state of the object
                @Override
                public void successCallback(String channel, Object message) {
                    System.out.println(message);
                    try {
                    	System.out.println("successCallback");
                        System.out.println(myData.toString(1));
                    } catch (org.json.JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // Called whenever any error occurs
                @Override
                public void errorCallback(String channel, PubnubError error) {
                    System.out.println("ERROR " + System.currentTimeMillis() / 1000
                            + " : " + error);
                }

            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

    }

}
