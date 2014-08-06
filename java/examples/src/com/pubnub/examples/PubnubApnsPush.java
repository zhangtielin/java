package com.pubnub.examples;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

public class PubnubApnsPush {


    public static void main(String[] args) {
        PubnubCrypto pc = new PubnubCrypto("abcd");

        Pubnub pubnub = new Pubnub("pub-c-6d82cd87-cd15-461c-8de6-d0330419f439","sub-c-e0d8405a-b823-11e2-89ba-02ee2ddab7fe");

        /*
            {
                "pn_apns": {
                    "aps" : {
                        "alert": "Game update 49ers touchdown",
                        "badge": 2
                    },
                    "teams" : ["49ers", "raiders"],
                    "score" : ["7", "0"]
                },
                "onlineMessage" : "this is message that should only be presented as online message"
            }

        */
        JSONObject jso = new JSONObject();

        try {
            jso.put("onlineMessage", pc.encrypt("this is message that should only be presented as online message"));
            JSONObject apns = new JSONObject();

            JSONArray teams = new JSONArray();
            teams.put(pc.encrypt("49ers"));
            teams.put(pc.encrypt("raiders"));
            apns.put("teams", teams);

            JSONArray score = new JSONArray();
            score.put(pc.encrypt("7"));
            score.put(pc.encrypt("0"));

            apns.put("teams", teams);
            apns.put("score", score);


            JSONObject aps = new JSONObject();

            aps.put("alert", "Game update 49ers touchdown");
            aps.put("badge", 2);

            apns.put("aps", aps);

            jso.put("pn_apns", apns);


        } catch (Exception e) {
            e.printStackTrace();
        }

        pubnub.publish("apns", jso, new Callback(){
            public void successCallback(String channel, Object response) {
                System.out.println(response);
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error);
            }
        });


    }

}
