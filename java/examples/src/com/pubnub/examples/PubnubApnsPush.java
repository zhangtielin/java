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

        Pubnub pubnub = new Pubnub("pub-c-c077418d-f83c-4860-b213-2f6c77bde29a","sub-c-e8839098-f568-11e2-a11a-02ee2ddab7fe");

        /*
            {
                "pn_apns": {
                    "aps" : {
                        "alert": "Game update 49ers touchdown",
                        "badge": 2
                    },
                    "teams" : ["49ers", "raiders"],
                    "score" : [7, 0]
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

            jso.put("pn_apns", apns);

        } catch (Exception e) {
            e.printStackTrace();
        }

        pubnub.publish("abcd", jso, new Callback(){
            public void successCallback(String channel, Object response) {
                System.out.println(response);
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error);
            }
        });

    }

}
