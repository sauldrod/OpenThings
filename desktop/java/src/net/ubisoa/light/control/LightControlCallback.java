package net.ubisoa.light.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

import net.ubisoa.common.BaseResource;
import net.ubisoa.light.push.PushInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class LightControlCallback extends BaseResource {
	// TODO: Add documentation.
	
	private static String lastCallbackData = "{}";
	private HttpClient client = ((LightControlServer)getApplication()).getClient();
	
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

		String position = "0";
		String lamp = "off";
		
		System.out.println("Se recibio un ping");
		System.out.println(getQuery().toString());
		
		try {
			HttpGet get = new HttpGet("http://127.0.0.1/rfid/data?output=json");
			HttpResponse response = client.execute(get);
			HttpEntity entity1 = response.getEntity();
			String content = "";
			if (entity1 != null) {
				content = EntityUtils.toString(entity1);
				entity1.consumeContent();
			}
			
			JSONObject jsonObj = new JSONObject(content);
			JSONArray jsonArray = jsonObj.getJSONArray("events");
			String item = "";
			for (int i = jsonArray.length() - 1 ; i < jsonArray.length(); i++) {
				JSONObject jsonItem = jsonArray.getJSONObject(i);
				item = jsonItem.getString("id");
				System.out.println("IMPOETANTTTEEEE - tag:" + item);
			}
			if (new String("010b0a1fa7").equals(item)){
				position = "0";
				lamp = "on";
			} else if (new String("010b0a1848").equals(item)){
				position = "180";
				lamp = "off";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		//Set Lamp
			try {
				String enc = "UTF-8";
				String data = "blue=" + lamp;
				URL url = new URL("http://127.0.0.1/lamp/data");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(data);
				writer.flush();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				while (reader.readLine() != null);
				writer.close();
				reader.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		//Set Blind
			try {
				String enc = "UTF-8";
				String data = "position=" + position;
				URL url = new URL("http://127.0.0.1/blind/data");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(data);
				writer.flush();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				while (reader.readLine() != null);
				writer.close();
				reader.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return new StringRepresentation("Callback Recived", MediaType.TEXT_PLAIN);
	
	}
}
