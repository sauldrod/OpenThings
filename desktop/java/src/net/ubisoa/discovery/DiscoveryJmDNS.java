package net.ubisoa.discovery;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


public class DiscoveryJmDNS  {

		public final static String REMOTE_TYPE = "_openthings._tcp.local.";
		private static JmDNS mdnsServer;
	
		public DiscoveryJmDNS() throws IOException {
			mdnsServer = JmDNS.create();
		}

		public static void registerService(String name, String path, int port) throws IOException {
			//Values for a registry
	        final HashMap<String, String> txtRecord = new HashMap<String, String>();
	        txtRecord.put("txtvers", "1");
	        txtRecord.put("path", path);
	
	        
			// Register the service.
			mdnsServer = JmDNS.create();
			ServiceInfo service = ServiceInfo.create(REMOTE_TYPE, name, port,  0, 0, txtRecord);
			mdnsServer.registerService(service);
			System.out.println("Service "+ name +" is registered.");
		}

		public static JmDNS getMdnsServer() {
			return mdnsServer;
		}

}
