package net.ubisoa.light.control;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryCore;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import com.phidgets.AdvancedServoPhidget;

public class LightControlServer extends Application {

	private HttpClient client = Defaults.getHttpClient();
	
	
	public static void main(String[] args) throws Exception {

		
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8300);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new LightControlServer());
		component.start();
		DiscoveryCore.registerService("LightControl", "/?output=rdf", "RDF/N3", 8300);
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new BaseRouter(getContext());
		router.attach("/data/", LightControlResource.class);
		router.attach("/description/", LightControlDescription.class);
		return router;
	}
	
	
	public HttpClient getClient() {
		return client;
	}

}
