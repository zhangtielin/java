package com.pubnub.examples;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import java.util.ArrayList;

public class PubnubDemoConsole {


    public static Callback OurCallback = new Callback() {
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
            System.out.println("Message from channel channel " + channel + " : " + message.toString());

        }

        public void errorCallback(String channel, Object message) {
            if (message.toString().equals("")) {
                System.out.println("Channel channel " + channel + " assumed disconnected");
            } else {
                System.out.println("Error on channel " + channel);
            }
        }
    };


    static Pubnub pn;

    public static void main(String[] args) throws PubnubException, InterruptedException {


        pn = new Pubnub("demo", "demo", false);
        pn.setOrigin("foo");

        pn.setResumeOnReconnect(true);
        pn.setMaxRetries(2000000000);
        pn.setRetryInterval(10000);
        pn.setSubscribeTimeout(1500);

        subscribe("a");

        //Thread.sleep(3000);

        subscribe("z", "y", "x");

        while (true) {
            System.out.println("\r\n\r\nBEGIN!");
            System.out.println("before sleep");
            Thread.sleep(4000);
            System.out.println("after sleep. publishing message now");
            pn.publish("y", "some message", new Callback() {});

        }

    }

    public static void subscribe(String ...channels) throws PubnubException {
        ArrayList<String> channelArray = new ArrayList<String>();

        for (int i = 0; i < channels.length; i++) {
            channelArray.add(channels[i]);
        }

        pn.unsubscribeAll();
        pn.subscribe(channels, OurCallback);
    }

}
