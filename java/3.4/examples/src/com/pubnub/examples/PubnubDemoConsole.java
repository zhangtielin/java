package com.pubnub.examples;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import java.util.Hashtable;

public class PubnubDemoConsole {


    public static void main(String[] args) throws PubnubException {

        Pubnub pn;

        pn = new Pubnub("demo", "demo", false);
        //if (origin != null) {
        pn.setOrigin("foo.pubnub.com");
        //}
        pn.setResumeOnReconnect(true);
        pn.setMaxRetries(2000000000);
        pn.setRetryInterval(10000);
        //if (doHeardbeat) {
        // Set timeout 5s longer than our heartbeat send interval
        pn.setSubscribeTimeout(3000);
        //}

        Hashtable subArgs = new Hashtable(6);
        subArgs.put("channel", "x");
        pn.subscribe(subArgs, new Callback() {
            public void connectCallback(String channel, Object message) {
                System.out.println("Connected to channel " + channel);
            }

            public void disconnectCallback(String channel, Object message) {
                System.out.println("Disconnected from channel " + channel);

            }

            public void reconnectCallback(String channel, Object message) {
                System.out.println("Reconnected to channel channel " + channel);

            }

            public void successCallback(String channel, Object message) {
                System.out.println("Success from channel channel " + channel);

            }

            public void errorCallback(String channel, Object message) {
                if (message.toString() == "") {
                    System.out.println("Channel channel " + channel + " assumed disconnected");
                } else {
                    System.out.println("Error on channel " + channel);
                }
            }
        });


        subArgs.put("channel", "y");
        pn.subscribe(subArgs, new Callback() {
            public void connectCallback(String channel, Object message) {
                System.out.println("Connected to channel " + channel);
            }

            public void disconnectCallback(String channel, Object message) {
                System.out.println("Disconnected from channel " + channel);

            }

            public void reconnectCallback(String channel, Object message) {
                System.out.println("Reconnected to channel channel " + channel);

            }

            public void successCallback(String channel, Object message) {
                System.out.println("Success from channel channel " + channel);

            }

            public void errorCallback(String channel, Object message) {
                if (message.toString() == "") {
                    System.out.println("Channel channel " + channel + " assumed disconnected");
                } else {
                    System.out.println("Error on channel " + channel);
                }
            }
        });

    }

}
