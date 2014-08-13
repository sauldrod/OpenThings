/*
 * Copyright (c) 2010, Edgardo Avilés-López
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * – Redistributions of source code must retain the above copyright notice, this list of
 *   conditions and the following disclaimer.
 * – Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * – Neither the name of the CICESE Research Center nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ubisoa.light.control;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.ubisoa.common.BaseResource;
import net.ubisoa.common.HTMLTemplate;
import net.ubisoa.core.Defaults;
import net.ubisoa.light.push.PushInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class LightControlResource extends BaseResource {
	// TODO: Add documentation.
	
	private HttpClient client = ((LightControlServer)getApplication()).getClient();
	private PushInfo pushInfo = ((LightControlServer)getApplication()).getPushInfo();
	

	
	@Get
	public StringRepresentation items() {		
		String html = "<h2>Published Items</h2>", items = "";
		
		try {
			HttpGet get = new HttpGet("http://127.0.0.1/rfid/data?output=json");
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			String content = "";
			if (entity != null) {
				content = EntityUtils.toString(entity);
				entity.consumeContent();
			}
			
			JSONObject jsonObj = new JSONObject(content);
			JSONArray jsonArray = jsonObj.getJSONArray("events");
			items = "";
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonItem = jsonArray.getJSONObject(i);
				items = "<li><strong>" + jsonItem.getString("id") + ".</strong> " +
					jsonItem.getString("action") + "</li>" + items;
			}
			html += "<ul>" + items + "</ul>";
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		HTMLTemplate template = new HTMLTemplate("Light Control", html);
		template.setSubtitle("This is the light system controler.");
		template.getScripts().add("http://api.ubisoa.net/js/subscriber-test.js");
		template.getStylesheets().add("http://api.ubisoa.net/css/subscriber-test.css");
		return new StringRepresentation(template.getHTML(), MediaType.TEXT_HTML);
	}	
	
	@Post("form")
	public void acceptItem(Representation entity) {
			Form form = new Form(entity);
			
			String status = form.getFirstValue("status");
			if (status.equals("start")){
			sendSubscriptionRequest();
			
			}else if (status.equals("stop")){
			//TODO STOP APPLICATION	
			
			}
			
			setStatus(Status.REDIRECTION_PERMANENT);
			setLocationRef("/control/?t=" + (new Date()).getTime());
	}
	
	public void sendSubscriptionRequest() {
		try {
			List<NameValuePair> params = new Vector<NameValuePair>();
			params.add(new BasicNameValuePair("hub.callback", pushInfo.getCallback()));
			params.add(new BasicNameValuePair("hub.mode", "subscribe"));
			params.add(new BasicNameValuePair("hub.topic", pushInfo.getTopic()));
			params.add(new BasicNameValuePair("hub.verify", "async"));
			params.add(new BasicNameValuePair("hub.verify_token", pushInfo.getToken()));
			UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params, "UTF-8");
			HttpPost post = new HttpPost(pushInfo.getHub());
			post.setEntity(paramsEntity);
			Defaults.getHttpClient().execute(post);
			getLogger().info("The Hubbub subscription was sent successfully.");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
