package net.ubisoa.discovery;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


public class DiscoveryCoreJmDNS  {

public final static String REMOTE_TYPE = "_openthings._tcp.local.";

	  public static void main(String[] args) throws IOException{	
		// Start DNS Service.
		  registerService("PRUBA2","/BLIND",80);
		  registerService("PRUBA3","/BLIND",80);
	  }	
		
		public static void registerService(String name, String path, int port) throws IOException {
			JmDNS mdnsServer = JmDNS.create();
			//Values for a registry
	        final HashMap<String, String> txtRecord = new HashMap<String, String>();
	        txtRecord.put("txtvers", "1");
	        txtRecord.put("path", path);
	
	        
			// Register a test service.
			ServiceInfo testService = ServiceInfo.create(REMOTE_TYPE, name, 80,  0, 0, txtRecord);
			mdnsServer.registerService(testService);
			System.out.println("Service "+ name +" is registered.");
		}

}
