package com.pubnub.api;

class NonSubscribeManager extends AbstractNonSubscribeManager {

    public NonSubscribeManager(String name, int connectionTimeout,
                               int requestTimeout) {
        super(name, connectionTimeout, requestTimeout);
    }
    
    public NonSubscribeManager(String name, int connectionTimeout,
            int requestTimeout, int workers) {
    	super(name, connectionTimeout, requestTimeout, workers);
    }

    public void clearRequestQueue() {
        _waiting.clear();
    }
}
