package net.ubisoa.light.context;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.atom.AtomConverter;
import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Text;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

public class ContextResource extends BaseResource{
	
	@Get("html")
	public StringRepresentation servoMotor() throws PhidgetException {
		
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		
		String html = "Context Server";
		
		HTMLTemplate template = new HTMLTemplate("Context Server", html);
		template.setSubtitle("This is a local context server.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	

	@Get("rdf")
	public StringRepresentation servoRDF() throws PhidgetException {

				
		String rdf = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
				"<rdf:RDF"+
				"  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+
				"  xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""+
				"  xmlns:spt=\"http://spitfire-project.eu/ontology/ns/\""+
				"  xmlns:ssn=\"http://purl.oclc.org/NET/ssnx/ssn#\""+
				"  xmlns:hr=\"http://iserve.kmi.open.ac.uk/ns/hrests#\">"+

				// Service Description
				"  <ssn:Observation rdf:about=\""+getRequest().getRootRef().toString() +"\">"+
				"    <rdfs:label>Window Blind Position</rdfs:label>"+

				"    <rdfs:seeAlso rdf:resource=\""+getRequest().getRootRef().toString() +"/.well-known\"/>"+
				"  </hr:Observation>"+
				"</rdf:RDF>";
		
		return new StringRepresentation(rdf, MediaType.APPLICATION_RDF_XML);
	}
	
	
	
}
