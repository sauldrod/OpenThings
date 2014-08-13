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
import java.util.UUID;

import net.ubisoa.common.BaseRouter;
import net.ubisoa.core.Defaults;
import net.ubisoa.discovery.DiscoveryBonjour;
import net.ubisoa.discovery.DiscoveryJmDNS;
import net.ubisoa.light.push.PushApplication;
import net.ubisoa.light.push.PushInfo;

import org.apache.http.client.HttpClient;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author Edgardo Avilés-López <edgardo@ubisoa.net>
 */
public class LightControlServer extends Application implements PushApplication {
	// TODO: Add documentation.
	private HttpClient client = Defaults.getHttpClient();
	
	private final PushInfo pushInfo = new PushInfo(
			"http://127.0.0.1/", "http://127.0.0.1/rfid/data?output=json",
			"http://127.0.0.1/control/callback", UUID.randomUUID().toString());

	@Override
	public Restlet createInboundRoot() {
		
		DiscoveryBonjour.registerService("LightControl", "/control", 80);		
		
		Router router = new BaseRouter(getContext());
		router.attach("/", LightControlResource.class);
		router.attach("/callback", LightControlCallback.class);
		return router;
	}


	@Override
	public void pushCallback(String data) {
		getLogger().info("Received a push message!\n\t" + data);
	}
	
	public HttpClient getClient() {
		return client;
	}

	@Override
	public PushInfo getPushInfo() {
		return pushInfo;
	}
}
