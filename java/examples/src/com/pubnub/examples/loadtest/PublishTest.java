package com.pubnub.examples.loadtest;

import org.apache.commons.cli.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.*;

public class PublishTest {
	
	private static void usage(Options options){

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "Publisher", options );
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Options options = new Options();
		CommandLine cmd = null;
		
		String prefix = "";
		int noOfMessages = 10000;
		int workers = 100;
		String origin = "pubsub";
		String publish_key = "demo";
		String subscribe_key = "demo";
		String secret_key = "demo";
		boolean ssl = false;
		String cipher_key = null;
		String auth_key = null;
		
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("prefix").
				withType(String.class).withDescription("for creating unique channel names").create());
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("origin").
				withType(String.class).withDescription("Origin ( Ex. pubsub )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("publish_key").
				withType(String.class).withDescription("Publish Key ( default: 'demo' )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("subscribe_key").
				withType(String.class).withDescription("Subscribe Key ( default: 'demo' )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("secret_key").
				withType(String.class).withDescription("Secret Key ( default: 'demo' )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("cipher_key").
				withType(String.class).withDescription("Cipher Key ( default: null )").create());

		options.addOption(OptionBuilder.hasArg().withArgName("String").withLongOpt("auth_key").
				withType(String.class).withDescription("Auth Key").create());

		options.addOption(OptionBuilder.withLongOpt("ssl-on").withDescription("SSL on/off ? ( default: off )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("int").withLongOpt("no_of_messages").
				withType(Number.class).withDescription("Number of Messages").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("int").withLongOpt("no_of_workers").
				withType(Number.class).withDescription("Number of Workers").create());

		
		CommandLineParser parser = new BasicParser();
		try {
			cmd = parser.parse( options, args);
		} catch (ParseException e1) {
			usage(options);return;
		}
		if (cmd.hasOption("prefix")) {
			prefix = cmd.getOptionValue("prefix");
		}		
		if (cmd.hasOption("no_of_workers")) {
			try {
				workers = Integer.parseInt(cmd.getOptionValue("no_of_workers"));
			} catch (Exception e) {
				System.out.println("Unable to parse no_of_workers value");
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("no_of_messages")) {
			try {
				noOfMessages = Integer.parseInt(cmd.getOptionValue("no_of_messages"));
			} catch (Exception e) {
				System.out.println("Unable to parse no_of_messages value");
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("prefix")) {
			try {
				prefix = cmd.getOptionValue("prefix") + "-";
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("origin")) {
			try {
				origin = cmd.getOptionValue("origin");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("publish_key")) {
			try {
				publish_key = cmd.getOptionValue("publish_key");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("subscribe_key")) {
			try {
				subscribe_key = cmd.getOptionValue("subscribe_key");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("secret_key")) {
			try {
				secret_key = cmd.getOptionValue("secret_key");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("cipher_key")) {
			try {
				cipher_key = cmd.getOptionValue("cipher_key");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}

		if (cmd.hasOption("auth_key")) {
			try {
				auth_key = cmd.getOptionValue("auth_key");
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("ssl-on")) {
			try {
				ssl = true;
			} catch (Exception e) {
				e.printStackTrace();
				usage(options);return;
			}
		}
		
		
		/*
		String prefix = (args.length > 0)?args[0] + "-":"";
		int noOfMessages = (args.length > 1)?Integer.parseInt(args[1]):10000;
		int workers = (args.length > 2)?Integer.parseInt(args[2]):10000;
		String origin = (args.length > 3)?args[3]:"pubsub";
		*/
		
		Pubnub pubnub = new Pubnub(publish_key, subscribe_key, secret_key, cipher_key, ssl, workers);
		
	    pubnub.setCacheBusting(false);
        pubnub.setOrigin(origin);

        if (auth_key != null) pubnub.setAuthKey(auth_key);

		//pn.setDomain("pubnub.co");  // only if required
		  

        
        
		JSONObject[] jsoArray = new JSONObject[noOfMessages];
		
		for ( int i = 0; i < noOfMessages; i++) {
			JSONObject jso = new JSONObject();
			try {
				jso.put("channel", "channel-" + prefix + (i+1));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsoArray[i] = jso;
		}
		
		final Publisher publisher = new Publisher(1234, noOfMessages, pubnub, jsoArray);
        pubnub.time(new Callback(){
        	@Override
        	public void successCallback(String channel, Object response) {
        		try {
					publisher.setStartTimetoken((Long)(new JSONArray(response.toString()).get(0)));
				} catch (JSONException e) {
					
				}
        	}
        });
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		publisher.init();
		publisher.start();
		
	}

}
