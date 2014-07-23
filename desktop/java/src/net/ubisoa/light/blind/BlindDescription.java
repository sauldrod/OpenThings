package net.ubisoa.light.blind;

import org.apache.http.client.HttpClient;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

import net.ubisoa.common.BaseResource;

public class BlindDescription  extends BaseResource{
	HttpClient client = ((BlindServer)getApplication()).getClient();
	
	@Get("rdf")
	public StringRepresentation servoRDF() throws PhidgetException {
				
		String rdf = "holo";
		
		return new StringRepresentation(rdf, MediaType.APPLICATION_RDF_XML);

	}
}