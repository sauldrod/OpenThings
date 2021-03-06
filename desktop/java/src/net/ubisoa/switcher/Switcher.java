package net.ubisoa.switcher;

import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryJmDNS;
import net.ubisoa.light.blind.BlindServer;
import net.ubisoa.light.classroom.ClassroomServer;
import net.ubisoa.light.control.LightControlServer;
import net.ubisoa.light.push.HubServer;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
//Class to start multiple services on the same port and path, multple instances of this module can be run
//on different ports or different devices to provide a distributed topology

public class Switcher {

	public static void main(String[] args) throws Exception {	
		
		//Set default server configuration on port 80
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 80);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		
		//Start DNS Server
		new DiscoveryJmDNS();
		
		//Virtual services selection and address definition - Uncomment for enable services.
		component.getDefaultHost().attach("", new HubServer());
		component.getDefaultHost().attach("/classroom", new ClassroomServer());
//		component.getDefaultHost().attach("/control", new LightControlServer());
//		component.getDefaultHost().attach("/blind", new BlindServer());
//		component.getDefaultHost().attach("/lamp", new LampServer());
//		component.getDefaultHost().attach("/rfid", new RFIDServer());

		//Start the server
		component.start();
	}
	
}
