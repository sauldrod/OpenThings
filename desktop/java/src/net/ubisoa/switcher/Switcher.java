package net.ubisoa.switcher;

import net.ubisoa.core.Defaults;
import net.ubisoa.light.blind.BlindServer;
import net.ubisoa.light.control.LightControl;
import net.ubisoa.light.lamp.LampServer;
import net.ubisoa.light.push.HubServer;
import net.ubisoa.light.rfid.RFIDServer;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

public class Switcher {

	public static void main(String[] args) throws Exception {	
		
		//Set default server configuration on port 80
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 80);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		
		component.getDefaultHost().attach("", new HubServer());
		component.getDefaultHost().attach("/rfid", new RFIDServer());
		component.getDefaultHost().attach("/control", new LightControl());
		component.getDefaultHost().attach("/blind", new BlindServer());
		component.getDefaultHost().attach("/lamp", new LampServer());

		
		//Start the server
		component.start();
	}
	
}
