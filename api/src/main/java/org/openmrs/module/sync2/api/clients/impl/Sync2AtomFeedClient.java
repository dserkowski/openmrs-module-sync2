package org.openmrs.module.sync2.api.clients.impl;

import java.net.URI;
import java.util.HashMap;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;

import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.sync2.api.clients.Client;
import org.openmrs.module.sync2.api.exceptions.Sync2Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

public class Sync2AtomFeedClient implements Client {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sync2AtomFeedClient.class);
	
	private final AtomFeedProperties atomFeedProperties;
	
	private URI uri;
	
	private AtomFeedClient atomFeedClient;
	
	@Autowired
	private PlatformTransactionManager springPlatformTransactionManager;
	
	public Sync2AtomFeedClient() {
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager = getAtomFeedSpringTransactionManager();
		HashMap<String, String> clientCookies = new HashMap<>();
		atomFeedProperties =  new AtomFeedProperties(); // TODO: make it more generic - create getters and setters
		
		atomFeedClient = new AtomFeedClient(
			new AllFeeds(atomFeedProperties, clientCookies),
			new AllMarkersJdbcImpl(atomFeedSpringTransactionManager),
			new AllFailedEventsJdbcImpl(atomFeedSpringTransactionManager),
			atomFeedProperties,
			getAtomFeedSpringTransactionManager(),
			uri,
			new Sync2FeedWorker()
		);
	}
	
	@Override
	public void process() {
		LOGGER.info("{} started processing", getClass().getName());
		validateConfiguration();
		atomFeedClient.processEvents();
		atomFeedClient.processFailedEvents();
	}
	
	private void validateConfiguration() {
		if (uri == null) {
			throw new Sync2Exception("URI is not set");
		}
	}
	
	private AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(springPlatformTransactionManager);
	}
	
	public URI getUri() {
		return uri;
	}
	
	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	public AtomFeedProperties getAtomFeedProperties() {
		return atomFeedProperties;
	}
}
	
