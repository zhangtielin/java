package com.pubnub.examples;

import com.pubnub.api.*;

import org.json.*;

public class SetData {

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
        String path = "b.c";

        final PubnubSyncedObject myData = pubnub.createSyncObject(objectId, path);

        JSONObject jso = new JSONObject();

        try {
            jso.put("x", 1);
            jso.put("y", 2);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        myData.merge(path, jso, new Callback() {

            // Called when this operation succeeds
            @Override
            public void successCallback(String channel, Object message) {
                System.out.println("set(): SUCCESS");

                System.out.println(message.toString());
                try {
                    System.out.println(myData.toString(1));
                } catch (org.json.JSONException e) {

                    e.printStackTrace();
                }

            }

            // Called if an error occurs
            @Override
            public void errorCallback(String channel, PubnubError error) {

                System.out.println(System.currentTimeMillis() / 1000 + " : "
                        + error);
            }

        });

    }

}
