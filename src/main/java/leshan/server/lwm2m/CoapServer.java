/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package leshan.server.lwm2m;

import leshan.server.LwM2mServer;
import leshan.server.lwm2m.client.ClientRegistry;
import leshan.server.lwm2m.resource.RegisterResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Lightweight M2M server.
 * <p>
 * This CoAP server defines a /rd resources as described in the CoRE RD specification. A {@link ClientRegistry} must be
 * provided to host the description of all the registered LW-M2M clients.
 * </p>
 * <p>
 * A {@link RequestHandler} is provided to perform server-initiated requests to LW-M2M clients.
 * </p>
 */
public class CoapServer {

    private ch.ethz.inf.vs.californium.server.Server coapServer;

    private static final Logger LOG = LoggerFactory.getLogger(LwM2mServer.class);

    /** IANA assigned UDP port for CoAP (so for LWM2M) */
    public static final int PORT = 5684;

    private final RequestHandler requestHandler;

    public CoapServer(ClientRegistry clientRegistry) {
        // init CoAP server
        coapServer = new ch.ethz.inf.vs.californium.server.Server(PORT);

        // define /rd resource
        RegisterResource rdResource = new RegisterResource(clientRegistry);
        coapServer.add(rdResource);

        this.requestHandler = new RequestHandler(coapServer.getEndpoints().get(0));
    }

    /**
     * Starts the server and binds it to assigned UDP port for LW-M2M (5684).
     */
    public void start() {
        coapServer.start();
        LOG.info("LW-M2M server started on port " + PORT);
    }

    /**
     * Stops the server and unbinds it from assigned port.
     */
    public void stop() {
        coapServer.stop();
    }

    public RequestHandler getRequestHandler() {
        return this.requestHandler;
    }
}
