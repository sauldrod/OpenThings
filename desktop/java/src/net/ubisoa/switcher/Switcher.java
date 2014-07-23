package net.ubisoa.switcher;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import net.ubisoa.core.Defaults;
import net.ubisoa.light.blind.BlindServer;

public class Switcher {

	public static void main(String[] args) throws Exception {	
		
		//Set default server configuration on port 80
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 80);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		
		component.getDefaultHost().attach("", new BlindServer());
//		component.getDefaultHost().attach("/", new LightControlServer());		
//		component.getDefaultHost().attach("/", new LampServer());
//		component.getDefaultHost().attach("/", new RFIDServer());
		
		//Start the server
		component.start();
	}
	
}
