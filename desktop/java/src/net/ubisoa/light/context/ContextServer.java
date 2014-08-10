package net.ubisoa.light.context;

import java.io.IOException;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryJmDNS;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

public class ContextServer extends Application {
	private HttpClient client = Defaults.getHttpClient();
	
	@Override
	public Restlet createInboundRoot() {
		
		//Set resource routes
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, ContextDescription.class);
		router.attach("/data", ContextResource.class);
		
		//Set cool redirect
		String target = "/context/data";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);
		router.attach("/", redirector);
		
		//Register service on dns-sd
		try {
			DiscoveryJmDNS.registerService("Context", "/context", 80);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return router;
	}

	//Interface to attach client to resource
	public HttpClient getClient() {
		return client;
	}

}
