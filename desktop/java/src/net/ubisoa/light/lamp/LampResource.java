package net.ubisoa.light.lamp;

import java.util.Date;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.light.lamp.LampServer;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.atom.AtomConverter;
import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Text;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

public class LampResource extends BaseResource{
	private static final int COLOR_BLUE = 0;
	
	private InterfaceKitPhidget phidget = ((LampServer)getApplication()).getPhidget();

	private boolean ledIsOn(int ledId) {
		try {
			return phidget.getOutputState(ledId);
		} catch (PhidgetException e) {
			return false;
		}
	}
	
	@Get("html")
	public StringRepresentation semaphore() {
		String html = "<script type=\"text/javascript\">" +
				"function click(sender) { var target = $('#' + $(sender).html().toLowerCase()); target.attr('checked', $(sender).hasClass('on') ? '' : 'checked'); $(sender).toggleClass('on'); $('#form').submit(); }" +
				"</script>" +
			"<style>.led { border: 2px solid black; border-radius: 50px; width: 50px; " +
			"padding: 15px 0; text-align: center; float: left; margin-right: 4px; background: " +
			"-webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, from(#EEE), to(#AAA)); " +
			"color: #555; text-shadow: rgba(255, 255, 255, 0.8) 0 1px 0; cursor: pointer; } .led.on { " +
			"border-color: black; color: white; text-shadow: black 0px -1px 0px; } .led.blue.on " +
			"{ background: -webkit-gradient(radial, 50% 30%, 0, 50% 0%, 50, from(#6AC1FF), " +
			"to(#2C63FF)); } </style>" +
			"<div class=\"led blue " + (ledIsOn(COLOR_BLUE)? "on" : "off") + "\" onclick=\"click(this)\">Blue</div>" +
			"<div style=\"clear: both\"><br /><form id=\"form\" method=\"POST\"><div style=\"display:none\">" +
			"<input type=\"checkbox\" id=\"blue\" name=\"blue\" value=\"on\"" +
			(ledIsOn(COLOR_BLUE)? "checked": "") + "/> Lamp<br />" +
			"<input type=\"submit\" value=\"Submit\" /></div></form>";
		
		HTMLTemplate template = new HTMLTemplate("Lamp Server", html);
		template.setSubtitle("This is a lamp server.");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}
	
	@Get("xml")
	public DomRepresentation semaphoreXML() {
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root = d.createElement("lamp"), child;
			d.appendChild(root);
			
			child = d.createElement("blue");
			child.appendChild(d.createTextNode((ledIsOn(COLOR_BLUE)? "on" : "off")));
			root.appendChild(child);
				
			d.normalizeDocument();
			return new DomRepresentation(MediaType.TEXT_XML, d);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Get("atom")
	public Representation semaphoreAtom() {
		String html ="<table>" +
			"<tr><th>Blue</th><td>" + (ledIsOn(COLOR_BLUE)? "on" : "off") + "</td></tr>" +
			"</table>";
		Feed feed = new Feed();
		Entry entry = new Entry();
		entry.setId("urn:uuid:" + UUID.randomUUID());
		entry.setTitle(new Text("Lamp Status"));
		Content content = new Content();
		content.setInlineContent(new StringRepresentation(html, MediaType.TEXT_HTML));
		entry.setContent(content);
		feed.getEntries().add(entry);
		
		AtomConverter atomConverter = new AtomConverter();
		return atomConverter.toRepresentation(feed, new Variant(MediaType.APPLICATION_ATOM), this);
	}
	
	@Get("json")
	public JsonRepresentation semaphoreJson() {
		String padding = getQuery().getFirstValue("callback");
		try {
			JSONObject json = new JSONObject();
			json.put("blue", ledIsOn(COLOR_BLUE)? "on" : "off");
			String jsonStr = json.toString();
			if (padding != null)
				jsonStr = padding + "(" + jsonStr + ")";
			return new JsonRepresentation(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setStatus(Status.SERVER_ERROR_INTERNAL);
		return null;
	}
	
	@Post("form")
	public void acceptItem(Representation entity) {
		try {
			Form form = new Form(entity);
		
			String blue = form.getFirstValue("blue");
			boolean blueIsOn = blue != null && blue.equals("on");
				phidget.setOutputState(COLOR_BLUE, blueIsOn);
		
			
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/?t=" + (new Date()).getTime());
		} catch (PhidgetException e) {
			setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
			setLocationRef("/?t=" + (new Date()).getTime());
		}
	}
	
}
