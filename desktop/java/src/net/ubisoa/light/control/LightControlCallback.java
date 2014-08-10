package net.ubisoa.light.control;

import java.io.IOException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.light.push.PushApplication;
import net.ubisoa.light.push.PushInfo;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class LightControlCallback extends BaseResource {
	// TODO: Add documentation.
	
	private static String lastCallbackData = "{}";
	
	@Get("html")
	public StringRepresentation htmlCallback(Representation entity) {
		String mode = getQuery().getFirstValue("hub.mode");
		String topic = getQuery().getFirstValue("hub.topic");
		String challenge = getQuery().getFirstValue("hub.challenge");
		String token = getQuery().getFirstValue("hub.verify_token");
		PushInfo pushInfo = ((LightControlServer)getApplication()).getPushInfo();
		
		if (mode != null && (mode.compareTo("subscribe") == 0 ||
				mode.compareTo("unsubscribe") == 0)) {
			if (pushInfo.getTopic().compareTo(topic) == 0 &&
				pushInfo.getToken().compareTo(token) == 0)				
				return new StringRepresentation(challenge, MediaType.TEXT_PLAIN);
		}
		
		
		
		return new StringRepresentation("Waiting callbacks…", MediaType.TEXT_PLAIN);
	}
	
	@Post("json")
	public StringRepresentation jsonCallback(Representation entity) {
	
		System.out.println("Se recibio un ping");
		System.out.println(getQuery().toString());
		
		return new StringRepresentation("Callback Recived", MediaType.TEXT_PLAIN);
	
	}
}
