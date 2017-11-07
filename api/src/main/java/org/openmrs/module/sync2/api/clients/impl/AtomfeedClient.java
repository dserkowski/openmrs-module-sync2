package org.openmrs.module.sync2.api.clients.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.openmrs.module.sync2.api.clients.Client;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;
import org.ict4h.atomfeed.client.service.*;

public class AtomfeedClient implements Client {
	
	private URI uri;
	
	private AtomFeedClient atomFeedClient;
	
	public AtomfeedClient() {
		// TODO: change it to constructor with params - it is only for tests
		String feedUri = "http://localhost:8080/feed/recent";
		
		try {
			this.uri = new URI(feedUri);
		} catch (URISyntaxException e) {
			throw new Sync2Exception("Provided incorrect feed URI:" + feedUri, e);
		}
		
	}
	
	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
	}
}
