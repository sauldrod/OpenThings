package net.ubisoa.discovery;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;


public class DiscoveryJmDNS  {
	private static String discoveryVersionNumber = "1.0.1";
	
		public final static String REMOTE_TYPE = "_openthings._tcp.local.";
		private static JmDNS mdnsServer;
	
		public DiscoveryJmDNS(){
			try {
				mdnsServer = JmDNS.create();
				mdnsServer.unregisterAllServices();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Discovery: JmDNS Service Created");
		}

		public static void registerService(String name, String path, int port) {
			//Values for a registry
	        final HashMap<String, String> txtRecord = new HashMap<String, String>();
	        txtRecord.put("path", path);
	        txtRecord.put("txtvers", discoveryVersionNumber);
	        
	        
			// Register the service.
			ServiceInfo service = ServiceInfo.create(REMOTE_TYPE, name, port,  0, 0, txtRecord);
			try {
				mdnsServer.registerService(service);
			} catch (IOException e) {
				System.out.println("Discovery: Service "+ name +" not registered.");
				e.printStackTrace();
				return;
			}
			System.out.println("Discovery: Service "+ name +" is registered.");

		}

		public static JmDNS getMdnsServer() {
			return mdnsServer;
		}
		
		

}
