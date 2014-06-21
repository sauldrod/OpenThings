package net.ubisoa.light.blind;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.common.CoolRedirector;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryCore;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Form;
import org.restlet.data.Protocol;
import org.restlet.routing.Extractor;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

public class BlindServer extends Application {
	private static AdvancedServoPhidget phidget;
	private HttpClient client = Defaults.getHttpClient();
	
	private static boolean connectPhidget() {
		try {
			phidget = new AdvancedServoPhidget();
			phidget.open(177475);
			System.out.println("waiting for Servo attachment...");
			phidget.waitForAttachment();
			System.out.println("Serial: " + phidget.getSerialNumber());
			System.out.println("Servos: " + phidget.getMotorCount());			
			phidget.setEngaged(0, true);
		    phidget.setServoType(0,AdvancedServoPhidget.PHIDGET_SERVO_HITEC_HS422);
			phidget.setPosition(0, 0);
			return true;
		} catch (PhidgetException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {	
		if (!connectPhidget()) {
			System.out.println("Cannot start Phidget.");
			return;
		}
		
		Component component = new Component();
		Server server = new Server(Protocol.HTTP, 8300);
		component.getServers().add(server);
		server.getContext().getParameters().set("maxTotalConnections", Defaults.MAX_CONNECTIONS);
		server.getContext().getParameters().set("maxThreads", Defaults.MAX_THREADS);
		component.getDefaultHost().attach(new BlindServer());
		component.start();
		DiscoveryCore.registerService("Blind", "/description/?output=rdf", "RDF/N3", 8300);
	}
	
	@Override
	public Restlet createInboundRoot() {
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, BlindDescription.class);
		router.attach("/", BlindResource.class);
		
		String target = "/data";
		Redirector redirector = new CoolRedirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);

		// Attach the extractor to the router
		router.attach("/data", redirector);
		
		
		return router;
	}
	
	public AdvancedServoPhidget getPhidget() {
		return phidget;
	}
	
	public HttpClient getClient() {
		return client;
	}

}
