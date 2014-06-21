package net.ubisoa.light.blind;

import org.apache.http.client.HttpClient;
import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

import net.ubisoa.common.BaseResource;

public class BlindDescription  extends BaseResource{
	private AdvancedServoPhidget phidget = ((BlindServer)getApplication()).getPhidget();
	HttpClient client = ((BlindServer)getApplication()).getClient();
	
	@Get("rdf")
	public Representation servoRdf() throws PhidgetException{
		String ssn = "http://purl.oclc.org/NET/ssnx/ssn#";
		String spt = "http://spitfire-project.eu/ontology/ns/";
		
		String root = getRequest().getRootRef().toString();
		
		
		Reference servoRef = new Reference(root);
		Reference actuator = new Reference(spt + "Actuator"); 
		Reference maxValue = new Reference(spt + "maxValue");
		Reference minValue = new Reference(spt + "minValue");
		Reference value = new Reference(spt + "Value");		
		
		Graph servoGraph = new Graph();
		servoGraph.add(servoRef, "a", actuator);
		servoGraph.add(servoRef, maxValue, new Literal(Double.toString(phidget.getPositionMax(0))));
		servoGraph.add(servoRef, minValue, new Literal(Double.toString(phidget.getPositionMin(0))));
		servoGraph.add(servoRef, value, new Literal(Double.toString(phidget.getCurrent(0))));
		
		return servoGraph.getRdfTurtleRepresentation();
		
		
	}
}
