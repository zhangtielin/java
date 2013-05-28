package com.pubnub.examples.BatteryTest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.examples.BatteryTest.R;



abstract class Suite {
	public abstract void runSuite();
	public abstract void stopSuite();
	private String menuStr;
	Suite(String menuStr) {
		this.menuStr = menuStr;
		BatteryTest.bt.listItems.add(this.menuStr);
	}
}

class PublishThread implements Runnable {
	private int interval;
	private String message;
	private String channel;
	private volatile boolean run = true;
	
	PublishThread(String channel, String message, int interval) {
		this.interval = interval;
		this.message = message;
		this.channel = channel;
	}
	
	public void stop() {
		run = false;
	}
	
	@Override
	public void run() {
		while(run) {
			Pubnub pn = BatteryTest.bt.pubnub;
			pn.publish(channel, message, new Callback() {});
			try {
				Thread.sleep(this.interval * 1000);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
}


class PublishSuite extends Suite {
	int messageSize;
	int interval;
	String channel;
	PublishThread pt;
	PublishSuite(String channel, int messageSize, int interval) {
		super("PUBLISH : " + channel + " : " + messageSize + " : " + interval);
		this.channel = channel;
		this.messageSize = messageSize;
		this.interval = interval;
	}
	public void runSuite() {
		char[] arr = new char[messageSize];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 'a';
		}
		String msg = new String(arr);
		pt = new PublishThread(channel, msg, interval);
		new Thread(pt).start();
	}
	@Override
	public void stopSuite() {
		if (pt != null) pt.stop();
	}
}

class SubscribeSuite extends Suite {

	SubscribeSuite(String menuStr) {
		super(menuStr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void runSuite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopSuite() {
		// TODO Auto-generated method stub
		
	}
	
}

class HistorySuite extends Suite {

	HistorySuite(String menuStr) {
		super(menuStr);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void runSuite() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopSuite() {
		// TODO Auto-generated method stub
		
	}
	
}


public class BatteryTest extends Activity {
	
	public static BatteryTest bt;
	
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    
    private double testStartBU = 100.0;
    
    List<Suite> testSuites = new ArrayList<Suite>();
    
    Pubnub pubnub = new Pubnub("demo", "demo", "", false);
    
    private void notifyUser(Object message) {
        try {
            if (message instanceof JSONObject) {
                final JSONObject obj = (JSONObject) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj.toString(),
                                Toast.LENGTH_LONG).show();

                        Log.i("Received msg : ", String.valueOf(obj));
                    }
                });

            } else if (message instanceof String) {
                final String obj = (String) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj,
                                Toast.LENGTH_LONG).show();
                        Log.i("Received msg : ", obj.toString());
                    }
                });

            } else if (message instanceof JSONArray) {
                final JSONArray obj = (JSONArray) message;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), obj.toString(),
                                Toast.LENGTH_LONG).show();
                        Log.i("Received msg : ", obj.toString());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double getBatteryLevel() {
    	Intent batteryIntent = this.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    	int rawlevel = batteryIntent.getIntExtra("level", -1);
    	double scale = batteryIntent.getIntExtra("scale", -1);
    	double level = -1;
    	if (rawlevel >= 0 && scale > 0) {
    		level = rawlevel / scale;
    	}
		return level;
    					
    }
    
    private void showBatteryUsage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test Result");
        builder.setMessage("Test Stopped. Battery Usage " + (testStartBU - getBatteryLevel()));
        final TextView textView = new TextView(this);
        builder.setView(textView);
        builder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	bt = this;
        super.onCreate(savedInstanceState);
        notifyUser("On Create");
        setContentView(R.layout.main);
        adapter=new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1,
            listItems);
        ListView lvItems = (ListView) findViewById(R.id.listView1);
        lvItems.setAdapter(adapter);
        lvItems.setTextFilterEnabled(true);
        
        Button btnClearAll = (Button) findViewById(R.id.btnClearAll);
        btnClearAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				testSuites.clear();
				listItems.clear();
				adapter.notifyDataSetChanged();
			}});
        
        Button btnStartTest = (Button) findViewById(R.id.btnStartTest);
        btnStartTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				testStartBU = getBatteryLevel();
				synchronized(testSuites) {
					for (Suite s : testSuites) {
						s.runSuite();
					}
				}
			}});
        
        Button btnStopTest = (Button) findViewById(R.id.btnStopTest);
        btnStopTest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				synchronized(testSuites) {
					for (Suite s : testSuites) {
						s.stopSuite();
					}
				}
				showBatteryUsage();
			}});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case R.id.option1:
            subscribe();
            return true;

        case R.id.option2:
            publish();
            return true;
            
        case R.id.option3:
            history();
            return true;


        
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void history() {
		// TODO Auto-generated method stub
		
	}

	private void publish() {
		Intent nextScreen = new Intent(getApplicationContext(), PublishActivity.class);
		startActivity(nextScreen);
	}

	private void subscribe() {
		// TODO Auto-generated method stub
		
	}

    

    

    

    
    
}
