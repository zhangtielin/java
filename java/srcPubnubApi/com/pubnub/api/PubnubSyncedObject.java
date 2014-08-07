package com.pubnub.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class DataSyncTransaction {
	String transId;
	JSONArray updatesArray = new JSONArray();
	boolean transactionComplete = false;
}

public class PubnubSyncedObject extends JSONObject {
	private DataSyncCallback dsCallback;
	private String objectId;
	private String path = "";
	private int depth = 0;
	private String slashPath = "";
	JSONObject meta = new JSONObject();
	private Hashtable transactionsTable = new Hashtable();
	private boolean stale = true;
	private boolean synced = false;

	private PubnubCore pubnub;

	PubnubCore getPubnub() {
		return pubnub;
	}

	void setPubnub(PubnubCore pubnub) {
		this.pubnub = pubnub;
	}

	private PubnubSyncedObject o = this;

	public PubnubSyncedObject(String objectId, String path) {
		this.objectId = objectId;
		this.path = path;

		if (this.path != null && this.path.length() > 0) {
			depth = PubnubUtil.splitString(this.path, ".").length;
			this.slashPath = this.path.replace(".", "/");
		}
	}

	public PubnubSyncedObject(String objectId) {
		this.objectId = objectId;
	}

	public DataSyncCallback getCallback() {
		return dsCallback;
	}

	private String getStringFromJSONObject(JSONObject o, String key) {
		try {
			return o.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private JSONObject getJSONObjectFromJSONObject(JSONObject o, String key) {
		try {
			return o.getJSONObject(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private Object getObjectFromJSONObject(JSONObject o, String key) {
		try {
			return o.get(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private String applyUpdate(JSONObject o, JSONObject update)
			throws JSONException {
		String location = update.getString("location");
		String[] path = PubnubUtil.splitString(location, ".");
		JSONObject[] pathNodes = new JSONObject[path.length];
		String last = path[path.length - 1];
		JSONObject x = o;
		pathNodes[0] = o;
		String updateAt = null;

		for (int i = 1 + depth; i < path.length - 1; i++) {
			String key = path[i];

			if (getJSONObjectFromJSONObject(x, key) == null) {
				if (updateAt == null) {
					updateAt = PubnubUtil.joinString(path, ".", 0, i);
				}
				x.put(key, new JSONObject());
			}
			x = x.getJSONObject(key);
			pathNodes[i] = x;
		}

		if (update.getString("action").equals("update")) {
			x.put(last, update.get("value"));
		} else if (update.getString("action").equals("delete")) {
			x.remove(last);
			for (int i = path.length - 2; i >= 2 + depth; i--) {
				if (pathNodes[i] != null && pathNodes[i].length() == 0) {
					pathNodes[i - 1].remove(path[i]);
					updateAt = PubnubUtil.joinString(path, ".", 0, i - 1);
				}
			}

			if (pathNodes[1 + depth] != null
					&& pathNodes[1 + depth].length() == 0) {
				o.remove(path[1 + depth]);
				updateAt = PubnubUtil.joinString(path, ".", 0, depth);
			}
		}
		if (meta != null)
			meta.put("last_update", update.getLong("timetoken"));
		
		return updateAt;

	}

	private void applyTransaction(JSONObject o, DataSyncTransaction dst,
			DataSyncCallback dsCallback) throws JSONException {

		JSONArray updatesArray = dst.updatesArray;
		JSONArray locations = new JSONArray();
		JSONObject cbData = new JSONObject();
		String updateAt = "";

		for (int i = 0; i < updatesArray.length(); i++) {
			JSONObject update = (JSONObject) updatesArray.get(i);
			updateAt = applyUpdate(o, update);
			update.remove("trans_id");
			update.remove("timetoken");
			String location = getStringFromJSONObject(update, "location");
			if (location != null) {
				location = location.substring(6);
				update.put("location", location);
				locations.put(location);
			}
		}
		((PubnubSyncedObject) o).setStale(false);
		cbData.put("location", locations);
		cbData.put("update_at", updateAt.substring(6));
		if (dsCallback != null)
			dsCallback.onUpdate(null);
	}

	private void applyAllTransactions(DataSyncCallback dsCallback) throws JSONException {
		Iterator keys = transactionsTable.entrySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			DataSyncTransaction dst = (DataSyncTransaction) transactionsTable
					.get(key);
			if (dst != null && dst.transactionComplete) {
				applyTransaction(o, dst, dsCallback);
				transactionsTable.remove(key);
			}
		}
	}

	public void initSync(final DataSyncCallback callback) throws PubnubException {
		this.dsCallback = callback;

		try {
			long timetoken = 0;
			if (meta != null)
				timetoken = meta.getLong("last_update");
			String[] channels = new String[] {
					"pn_ds_"
							+ objectId
							+ ((path != null && path.length() > 0) ? "." + path
									: ""),
					"pn_ds_"
							+ objectId
							+ ((path != null && path.length() > 0) ? "." + path
									: "") + ".*", "pn_dstr_" + objectId };

			pubnub.subscribe(channels, new Callback() {
				public void connectCallback(String channel, Object response) {
					if (((String) channel).equals("pn_ds_"
							+ objectId
							+ ((path != null && path.length() > 0) ? "." + path
									: ""))
							&& !synced) {
						sync(new Callback() {
							public void successCallback(String channel,
									Object response) {
								// callback.successCallback("", response);
								try {
									applyAllTransactions(null);
									synced = true;
									setStale(false);
									dsCallback.onReady(null);
								} catch (JSONException e) {
									dsCallback.onError(null);
								}
							}

							public void errorCallback(String channel,
									PubnubError error) {
								dsCallback.onError(null);
								synced = false;
							}
						});
					}
				}

				public void successCallback(String channel, Object response) {
					JSONObject update = (JSONObject) response;
					if (getStringFromJSONObject(update, "action") != null) {
						// save update
						try {
							String transactionId = update.getString("trans_id");
							DataSyncTransaction dst = (DataSyncTransaction) transactionsTable
									.get(transactionId);
							if (dst == null) {
								dst = new DataSyncTransaction();
								transactionsTable.put(transactionId, dst);
							}
							dst.updatesArray.put(update);

						} catch (JSONException e) {
							dsCallback.onError(null);
						}
					} else if ("complete".equals(getStringFromJSONObject(
							update, "status"))) {
						// transaction complete
						String transactionId = null;
						try {
							transactionId = update.getString("trans_id");
						} catch (JSONException e1) {
							dsCallback.onError(null);
						}

						if (transactionId == null)
							return;
						DataSyncTransaction dst = (DataSyncTransaction) transactionsTable
								.get(transactionId);
						if (dst == null)
							return;
						dst.transactionComplete = true;
						try {
							if (synced) {
								applyTransaction(o, dst, callback);
								transactionsTable.remove(transactionId);
							}
						} catch (JSONException e) {
							dsCallback.onError(null);
						}
					}

				}

				public void errorCallback(String channel, PubnubError error) {
					o.setStale(true);
					dsCallback.onError(null);
				}
			}, timetoken);
		} catch (JSONException e1) {
			dsCallback.onError(null);
		}
	}

	private static JSONObject deepMerge(JSONObject target, JSONObject source)
			throws JSONException {
		Iterator keys = source.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = source.get(key);
			if (!target.has(key)) {
				target.put(key, value);
			} else {
				if (value instanceof JSONObject) {
					JSONObject valueJson = (JSONObject) value;
					deepMerge(target.getJSONObject(key), valueJson);
				} else {
					target.put(key, value);
				}
			}
		}
		return target;
	}
	private static JSONObject put(JSONObject target, Object data, String path) throws JSONException {
		String[] pathArray = null;
		if (path != null && path.length() > 0) {
			pathArray = PubnubUtil.splitString(path, "/");
			for (int i = 0; i < pathArray.length - 1; i++) {
				target = target.getJSONObject(pathArray[i]);
			}
		}
		String key = pathArray[pathArray.length - 1];
		if (key != null) {
			target.put(key, data);
		}
		return target;
	}
	private void getByNextPage(final String objectId, final String path, final String nextPage, final Callback callback) {
		pubnub.get(objectId, path, nextPage, new Callback() {
			public void successCallback(String channel, Object response) {
				String nextPage = null;
				try {

					JSONObject data = getJSONObjectFromJSONObject((JSONObject)response,"payload");

					if (data != null)
						deepMerge(o, data);
					else 
						put(o,((JSONObject)response).get("payload"), path);
					
					nextPage = getStringFromJSONObject(((JSONObject)response), "next_page");
					
					if (nextPage != null && !nextPage.equals("null")) {
						getByNextPage(objectId, path, nextPage, callback);
					}
				} catch (JSONException e) {
					callback.errorCallback(channel, PubnubError.getErrorObject(
							PubnubError.PNERROBJ_INVALID_JSON, 11));
				}
				// o.setStale(false);
				try {
					if (meta != null)
						meta.put("last_update", System.nanoTime() / 100);
				} catch (JSONException e) {
					callback.errorCallback(channel, PubnubError.getErrorObject(
							PubnubError.PNERROBJ_INVALID_JSON, 12));
				}
				if (nextPage == null || nextPage.equals("null"))
					callback.successCallback("", "updated");
				// synced = true;
			}

			public void errorCallback(String channel, PubnubError response) {
				o.setStale(true);
				callback.errorCallback(channel, response);
			}
		});
	}
	private void sync(final Callback callback) {
		try {
			o.put("pn_ds_meta", new JSONObject());
			meta = o.getJSONObject("pn_ds_meta");
			meta.put("last_update", 0);
		} catch (JSONException e1) {
			callback.errorCallback("", PubnubError.getErrorObject(
					PubnubError.PNERROBJ_INVALID_JSON, 15));
		}

		getByNextPage(this.objectId, this.slashPath, null, callback);
	}

	public boolean isStale() {
		return stale;
	}

	void setStale(boolean stale) {
		this.stale = stale;
	}

	public void merge(String path, JSONObject jso, Callback callback) {
		pubnub.merge(objectId, path, jso, callback);
	}

	public void set(String path, JSONObject jso, Callback callback) {
		pubnub.set(objectId, path, jso, callback);
	}

	public void remove(String path, Callback callback) {
		pubnub.remove(objectId, path, callback);
	}
}
