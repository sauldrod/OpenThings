package net.ubisoa.light.blind;

import net.ubisoa.common.BaseResource;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import com.phidgets.PhidgetException;

public class BlindDescription  extends BaseResource{
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
"  <hr:Service rdf:about=\""+getRequest().getResourceRef().toString() +"\">"+
"    <rdf:type rdf:resource=\"http://purl.oclc.org/NET/ssnx/ssn#Device\"/>"+
"    <rdfs:label>Window Blind Description</rdfs:label>"+
"    <spt:actuate rdf:resource=\"http://dbpedia.org/resource/Window_blind\"/>"+
"    <ssn:featureOfInterest rdf:resource=\"http://spitfire-project.eu/ontology/ns/Light\"/>"+
"    <ssn:hasQuality rdf:resource=\"http://spitfire-project.eu/ontology/ns/actuate\"/>"+

// Interface Description

"    <hr:hasOperation>"+
"      <hr:Operation rdf:about=\""+getRequest().getResourceRef().toString() +"#op1\">"+
"        <rdfs:label>getPosition</rdfs:label>"+
"        <hr:hasAddress rdf:datatype=\"http://iserve.kmi.open.ac.uk/ns/hrests#URITemplate\">"+getRequest().getRootRef().toString() +"/data</hr:hasAddress>"+
"        <hr:hasMethod rdf:resource=\"http://www.w3.org/2011/http-methods#GET\"/>"+
"      </hr:Operation>"+
"    </hr:hasOperation>"+

"    <hr:hasOperation>"+
"      <hr:Operation rdf:about=\""+getRequest().getResourceRef().toString() +"#op2\">"+
"        <rdfs:label>setPosition</rdfs:label>"+
"        <hr:hasAddress rdf:datatype=\"http://iserve.kmi.open.ac.uk/ns/hrests#URITemplate\">"+getRequest().getRootRef().toString() +"/data?position={position}</hr:hasAddress>"+
"        <hr:hasMethod rdf:resource=\"http://www.w3.org/2011/http-methods#POST\"/>"+
"      </hr:Operation>"+
"    </hr:hasOperation>"+

"  </hr:Service>"+
"</rdf:RDF>";
		
		return new StringRepresentation(rdf, MediaType.APPLICATION_RDF_XML);

	}
}