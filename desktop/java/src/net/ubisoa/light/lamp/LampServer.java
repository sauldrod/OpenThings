package net.ubisoa.light.lamp;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryCore;
import net.ubisoa.light.blind.BlindDescription;
import net.ubisoa.light.blind.BlindResource;
import net.ubisoa.semaphore.SemaphoreResource;
import net.ubisoa.semaphore.SemaphoreServer;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;

public class LampServer extends Application {
	private static InterfaceKitPhidget phidget;

	private static boolean connectPhidget() {
		try {
			phidget = new InterfaceKitPhidget();
			phidget.openAny();
			phidget.waitForAttachment(1000);
			for (int i = 0; i < 4; i++)
				phidget.setOutputState(i, false);
			return true;
		} catch (PhidgetException e) {
			return false;
		}
	}
	
	@Override
	public Restlet createInboundRoot() {
		
		//Start interface
		if (!connectPhidget()) {
			System.out.println("Cannot start Phidget.");
		}
		
		//Set resource routes
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, LampDescription.class);
		router.attach("/data", LampResource.class);
		
		//Set cool redirect
		String target = "/lamp/data";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);
		router.attach("/", redirector);
		
		//Register service on dns-sd
		DiscoveryCore.registerService("Lamp", "/lamp", 80);		
		return router;
	}
	
	public InterfaceKitPhidget getPhidget() {
		return phidget;
	}
}
