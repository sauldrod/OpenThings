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
package net.ubisoa.light.rfid;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryBonjour;
import net.ubisoa.discovery.DiscoveryJmDNS;
import net.ubisoa.light.blind.BlindDescription;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class RFIDServer extends Application {
	private final Vector<RFIDEvent> events = new Vector<RFIDEvent>();
	private HttpClient client = Defaults.getHttpClient();


	@Override
	public Restlet createInboundRoot() {	
		//Start Reader
		Reader reader = new Reader();
	    reader.start();
		
		//Set resource routes
		Router router = new BaseRouter(getContext());
		router.attach(Defaults.WELL_KNOWN, BlindDescription.class);
		router.attach("/data", RFIDResource.class);
		
		//Set cool redirect
		String target = "/rfid/data";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_SEE_OTHER);
		router.attach("/", redirector);
		
		try {
			DiscoveryJmDNS.registerService("RFID", "/rfid", 80);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return router;
	}
	
	public List<RFIDEvent> getEvents() {
		return events;
	}
	
	public HttpClient getClient() {
		return client;
	}
}
