package net.ubisoa.light.classroom;

import java.io.IOException;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryBonjour;
import net.ubisoa.discovery.DiscoveryJmDNS;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

public class ClassroomServer extends Application {
	private HttpClient client = Defaults.getHttpClient();
	
	@Override
	public Restlet createInboundRoot() {
		
		
		
		//Set resource routes
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, ClassroomDescription.class);
		router.attach("/context", ClassroomContext.class);
		router.attach("/registry", ClassroomResource.class);
		router.attach("/status", ClassroomRegistry.class);
		
		//Set cool redirect
		String target = "/classroom/.well-known";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);
		router.attach("", redirector);
		
		//Chose between DiscoveryJmDNS and DiscoveryBonjour
		DiscoveryJmDNS.registerService("Classroom", "/classroom", 80);		
		return router;
	}

	//Interface to attach client to resource
	public HttpClient getClient() {
		return client;
	}

}
