package com.pubnub.examples.loadtest;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Pubnub;

public class SubscribeTest {

	private static void usage(Options options){

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "Subscriber", options );
	}

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Options options = new Options();
		CommandLine cmd = null;
		
		String prefix = "";
		int noOfChannels = 10000;
		int workers = 10000;
		String origin = "pubsub";
		int startIndex = 0;
		String publish_key = "demo";
		String subscribe_key = "demo";
		String secret_key = "demo";
		boolean ssl = false;
		String cipher_key = null;
		Long timetoken = 0L;
		
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
		
		options.addOption(OptionBuilder.hasArg().withArgName("Long").withLongOpt("timetoken").
				withType(Long.class).withDescription("Timetoken ( optional )").create());
		
		options.addOption(OptionBuilder.withLongOpt("ssl-on").
				withType(Boolean.class).withDescription("SSL on/off ? ( default: off )").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("int").withLongOpt("no_of_channels").
				withType(Number.class).withDescription("Number of Channels").create());
		
		options.addOption(OptionBuilder.hasArg().withArgName("int").withLongOpt("start_index").
				withType(Number.class).withDescription("Start Index. when == 200 , messages start at 201").create());
		
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
		
		if (cmd.hasOption("no_of_channels")) {
			try {
				noOfChannels = Integer.parseInt(cmd.getOptionValue("no_of_channels"));
			} catch (Exception e) {
				System.out.println("Unable to parse no_of_channels value");
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("start_index")) {
			try {
				startIndex = Integer.parseInt(cmd.getOptionValue("start_index"));
			} catch (Exception e) {
				System.out.println("Unable to parse start_index value");
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
				usage(options);return;
			}
		}
		
		if (cmd.hasOption("timetoken")) {
			try {
				timetoken = Long.parseLong(cmd.getOptionValue("timetoken"));
			} catch (Exception e) {
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
		int noOfChannels = (args.length > 1)?Integer.parseInt(args[1]):10000;
		int workers = (args.length > 2)?Integer.parseInt(args[2]):10000;
		String origin = (args.length > 3)?args[3]:"pubsub";
		int startIndex = (args.length > 4)?Integer.parseInt(args[4]):0; 
		
		*/
		
		String[] channels = new String[noOfChannels];
		
		for ( int i = 0; i < noOfChannels; i++) {
			channels[i] = "channel-" + prefix + ( startIndex + i+1);
		}
		
		Subscriber subscriber = new Subscriber(1234, channels, workers, origin,
				publish_key, subscribe_key, secret_key, cipher_key, ssl, timetoken);
		subscriber.init();
		subscriber.start();
	}
}
