package net.ubisoa.light.blind;

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

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;

public class BlindServer extends Application {
	private static AdvancedServoPhidget phidget;
	private HttpClient client = Defaults.getHttpClient();
	
	
	//Set interface configuration
	private static boolean connectPhidget() {
		try {
			phidget = new AdvancedServoPhidget();
//			phidget.open(177475);
			System.out.println("waiting for Servo attachment...");
//			phidget.waitForAttachment();
//			System.out.println("Serial: " + phidget.getSerialNumber());
//			System.out.println("Servos: " + phidget.getMotorCount());			
//			phidget.setEngaged(0, true);
//		    phidget.setServoType(0,AdvancedServoPhidget.PHIDGET_SERVO_HITEC_HS422);
//			phidget.setPosition(0, 0);
			return true;
		} catch (PhidgetException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public Restlet createInboundRoot() {
		
		//Start interface
//		if (!connectPhidget()) {
//			System.out.println("Cannot start Phidget.");
//		}
		
		//Set resource routes
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, BlindDescription.class);
		router.attach("/data", BlindResource.class);
		
		//Set cool redirect
		String target = "/blind/.well-known";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);
		router.attach("/", redirector);
		
		DiscoveryBonjour.registerService("Blind", "/blind/", 80);	
		return router;
	}
	
	//Interface to attach interface to resource
	public AdvancedServoPhidget getPhidget() {
		return phidget;
	}

	//Interface to attach client to resource
	public HttpClient getClient() {
		return client;
	}

}
