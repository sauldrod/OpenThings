package net.ubisoa.light.classroom;

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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

import net.ubisoa.jena.CheeseBase;
import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;

public class ClassroomContext extends BaseResource{
	
	//Web representation
	@Get("html")
	public StringRepresentation htmlClassroomContext() {
		String html = "<span>Send new context data</span><hr />"+
		"<span><a href=\"http://en.lodlive.it/?"+getRequest().getResourceRef().toString() +"\">Grafo lodlive</a></span>";
		
		HTMLTemplate template = new HTMLTemplate("Classroom Context", html);
		template.setSubtitle("This is the classroom related context.");
		
        
		
		// creates a new, empty in-memory model
        Model m = ModelFactory.createDefaultModel();

        // load some data into the model
        FileManager.get().readModel( m, CheeseBase.CHEESE_DATA_FILE );

        // generate some output
        showModelSize( m );
        listCheeses( m );
        
        
        
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	//Semantic representation
	@Get("rdf")
	public StringRepresentation rdfClassroomContext() {

				
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
	
    /***********************************/
    /* Internal implementation methods */
    /***********************************/

    /**
     * Show the size of the model on stdout
     */
    protected void showModelSize( Model m ) {
        System.out.println( String.format( "The model contains %d triples", m.size() ) );
    }

    /**
     * List the names of cheeses to stdout
     */
    protected void listCheeses( Model m ) {
        Resource cheeseClass = m.getResource( CheeseBase.CHEESE_SCHEMA + "Cheese" );

        StmtIterator i = m.listStatements( null, RDF.type, cheeseClass );

        while (i.hasNext()) {
            Resource cheese = i.next().getSubject();
            String label = getEnglishLabel( cheese );
            System.out.println( String.format( "Cheese %s has name: %s", cheese.getURI(), label ) );
        }
    }

    /**
     * Get the English-language label for a given resource. In general, a resource
     * may have zero, one or many labels. In this case, we happen to know that
     * the cheese resources have mutlilingual labels, so we pick out the English one
     * @param cheese
     * @return
     */
    protected String getEnglishLabel( Resource cheese ) {
        StmtIterator i = cheese.listProperties( RDFS.label );
        while (i.hasNext()) {
            com.hp.hpl.jena.rdf.model.Literal l = i.next().getLiteral();

            if (l.getLanguage() != null && l.getLanguage().equals( "en")) {
                // found the English language label
                return l.getLexicalForm();
            }
        }

        return "A Cheese with No Name!";
    }

    /**
     * Get the value of a property as a string, allowing for missing properties
     * @param r A resource
     * @param p The property whose value is wanted
     * @return The value of the <code>p</code> property of <code>r</code> as a string
     */
    protected String getValueAsString( Resource r, Property p ) {
        Statement s = r.getProperty( p );
        if (s == null) {
            return "";
        }
        else {
            return s.getObject().isResource() ? s.getResource().getURI() : s.getString();
        }
    }
	
}
