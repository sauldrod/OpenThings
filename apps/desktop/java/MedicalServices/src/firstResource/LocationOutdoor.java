package firstResource;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
/**
 * Resource that manages a list of items.
 * 
 */
public class LocationOutdoor extends BaseResource {
	String locationCode= new String();
	static String locationOutdoorURI    = "http://localhost:2122/ubicomp/location/outdoor/";
	static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/";
	//static String nm= "http://www.semanticweb.org/ontologies/2009/10/30/sentientVisor.owl";
	
	//Variables de la localizacion
	static String varLatitude;
	static String varLongitude;
	static String varAltitude;
	static String varName;
	
    public LocationOutdoor(Context context, Request request, Response response) {
        super(context, request, response);

        // Allow modifications of this resource via POST requests.
        setModifiable(true);

        // Declare the kind of representations supported by this resource.
        getVariants().add(new Variant(MediaType.TEXT_XML));
        
        locationCode =  request.getAttributes().get("locationCode").toString();  
        locationOutdoorURI = locationOutdoorURI + locationCode;  
    }

    /**
     * Handle POST requests: create a new item.
     */
    @Override
    public void acceptRepresentation(Representation entity)
            throws ResourceException {
        // Parse the given representation and retrieve pairs of
        // "name=value" tokens.
        Form form = new Form(entity);
        String itemName = form.getFirstValue("name");
        String itemDescription = form.getFirstValue("description");

        // Check that the item is not already registered.
        if (getItems().containsKey(itemName)) {
            generateErrorRepresentation(
                    "Item " + itemName + " already exists.", "1", getResponse());
        } else {
            // Register the new item
            getItems().put(itemName, new Item(itemName, itemDescription));

            // Set the response's status and entity
            getResponse().setStatus(Status.SUCCESS_CREATED);
            Representation rep = new StringRepresentation("Item created",
                    MediaType.TEXT_PLAIN);
            // Indicates where is located the new resource.
            rep.setIdentifier(getRequest().getResourceRef().getIdentifier()
                    + "/" + itemName);
            getResponse().setEntity(rep);
        }
    }

    /**
     * Returns a listing of all registered items.
     */
    @Override
    public Representation represent(Variant variant) throws ResourceException {
    	// Se crea el modelo vacio
        Model model = ModelFactory.createDefaultModel();
        
    	//Properties
    	Property altitude= model.createProperty(nm, "altitude");
    	Property longitude= model.createProperty(nm, "longitude");
    	Property latitude = model.createProperty(nm, "latitude");
    	Property name= model.createProperty(nm, "name");
    	   	
    	
 	
 		//Se crea el recurso
 		Resource locationOutdoor = model.createResource(locationOutdoorURI);
 		
 		//Se asignan los valores a las variables
 		varAltitude = "28.1987" ;
 		if(locationCode.equals("1111"))
 		{
 			varLongitude = "5";
 			varLatitude = "30";
 		}
 		else if(locationCode.equals("1112"))
 		{
 			varLongitude = "-5";
 			varLatitude = "5";
 		}
 		else if(locationCode.equals("1113"))
 		{
 			varLongitude = "-5";
 			varLatitude = "-5";
 		}
 		else if(locationCode.equals("1114"))
 		{
 			varLongitude = "5";
 			varLatitude = "-5";
 		}
 		
 		
 		varName = "Place";
 			
 		//Se agregan las propiedades
 		locationOutdoor.addProperty(altitude,varAltitude);
 		locationOutdoor.addProperty(longitude,varLongitude);
 		locationOutdoor.addProperty(latitude, varLatitude);
 		locationOutdoor.addProperty(name, varName);
 		
 		
 		//Se define el namespace, para no poner j.0: se coloca las iniciales SV
		 model.setNsPrefix( "SV", nm );
 		 
 		 //Se imprime el archivo RDF
 		 model.write(System.out, "RDF/XML-ABBREV");
        
    	
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            try {
            	
            	DomRepresentation representation = new DomRepresentation(
                        MediaType.TEXT_XML);
                // Generate a DOM document representing the list of
                // items.
            	Document d = representation.getDocument();
                //Element r = d.createElement("items");
                Element nodeUbicomp = d.createElement("ubicomp");
                Element nodeLocation = d.createElement("location");
                d.appendChild(nodeUbicomp);
                
                	Element nodeOutdoor = d.createElement("outdoor");

                    
                	Element eltName = d.createElement("name");
                	eltName.appendChild(d.createTextNode(varName));
                	nodeOutdoor.appendChild(eltName);

                    Element eltLongitude = d.createElement("longitude");
                    eltLongitude.appendChild(d.createTextNode(varLongitude));
                    nodeOutdoor.appendChild(eltLongitude);
                    
                    Element eltLatitude = d.createElement("latitude");
                    eltLatitude.appendChild(d.createTextNode(varLatitude));
                    nodeOutdoor.appendChild(eltLatitude);
                    
                    Element eltAltitude = d.createElement("altitude");
                    eltAltitude.appendChild(d.createTextNode(varAltitude));
                    nodeOutdoor.appendChild(eltAltitude);
                    
                    nodeLocation.appendChild(nodeOutdoor);
                    nodeUbicomp.appendChild(nodeLocation);
               
                d.normalizeDocument();

                // Returns the XML representation of this document.
                return representation;
                
                
                       
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Generate an XML representation of an error response.
     * 
     * @param errorMessage
     *            the error message.
     * @param errorCode
     *            the error code.
     */
    private void generateErrorRepresentation(String errorMessage,
            String errorCode, Response response) {
        // This is an error
        response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        // Generate the output representation
        try {
            DomRepresentation representation = new DomRepresentation(
                    MediaType.TEXT_XML);
            // Generate a DOM document representing the list of
            // items.
            Document d = representation.getDocument();

            Element eltError = d.createElement("error");

            Element eltCode = d.createElement("code");
            eltCode.appendChild(d.createTextNode(errorCode));
            eltError.appendChild(eltCode);

            Element eltMessage = d.createElement("message");
            eltMessage.appendChild(d.createTextNode(errorMessage));
            eltError.appendChild(eltMessage);

            response.setEntity(representation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
